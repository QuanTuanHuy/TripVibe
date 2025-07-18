package service

import (
	"context"
	"fmt"
	"sync"
	"time"

	"github.com/google/uuid"
	"go.uber.org/zap"

	"github.com/quantuanhuy/lib/src/config"
	entity "github.com/quantuanhuy/lib/src/core/entity/async"
	msgEntity "github.com/quantuanhuy/lib/src/core/entity/msg"
	"github.com/quantuanhuy/lib/src/core/port"
)

// AsyncReplyHandler handles async replies and integrates with AsyncRequestManager
type AsyncReplyHandler struct {
	correlationMgr port.ICorrelationPort
	callbackMgr    port.ICallbackManager
	logger         *zap.Logger
	config         *config.AsyncConfig
	
	// Integration with AsyncRequestManager for WaitForReply
	requestManager *AsyncRequestManager
}

func NewAsyncReplyHandler(
	correlationMgr port.ICorrelationPort,
	callbackMgr port.ICallbackManager,
	logger *zap.Logger,
	config *config.AsyncConfig,
) port.IAsyncReplyHandler {
	return &AsyncReplyHandler{
		correlationMgr: correlationMgr,
		callbackMgr:    callbackMgr,
		logger:         logger,
		config:         config,
	}
}

// SetRequestManager allows integration with AsyncRequestManager for WaitForReply
func (h *AsyncReplyHandler) SetRequestManager(requestManager *AsyncRequestManager) {
	h.requestManager = requestManager
}

func (h *AsyncReplyHandler) HandleReply(ctx context.Context, reply *entity.AsyncReply) error {
	h.logger.Info("Processing async reply",
		zap.String("correlation_id", reply.CorrelationID),
		zap.String("status", string(reply.Status)))

	// Get correlation data
	correlationData, err := h.correlationMgr.GetCorrelation(ctx, reply.CorrelationID)
	if err != nil {
		h.logger.Error("Failed to get correlation data",
			zap.String("correlation_id", reply.CorrelationID),
			zap.Error(err))
		return err
	}

	// Update correlation data
	correlationData.UpdateStatus(reply.Status)
	if err := h.correlationMgr.UpdateCorrelation(ctx, reply.CorrelationID, correlationData); err != nil {
		h.logger.Error("Failed to update correlation data",
			zap.String("correlation_id", reply.CorrelationID),
			zap.Error(err))
		// Continue processing even if update fails
	}

	// Notify waiting requests (if any)
	if h.requestManager != nil {
		h.requestManager.NotifyReply(reply.CorrelationID, reply)
	}

	// Handle callback if present
	if callback, exists := h.callbackMgr.GetCallback(reply.CorrelationID); exists {
		h.executeCallback(reply.CorrelationID, callback, reply)
	}

	// Cleanup
	h.correlationMgr.RemoveCorrelation(ctx, reply.CorrelationID)
	h.callbackMgr.RemoveCallback(reply.CorrelationID)

	h.logger.Info("Completed async reply processing",
		zap.String("correlation_id", reply.CorrelationID),
		zap.String("status", string(reply.Status)),
		zap.Duration("duration", reply.Duration))

	return nil
}

func (h *AsyncReplyHandler) executeCallback(correlationID string, callback entity.AsyncCallback, reply *entity.AsyncReply) {
	go func() {
		defer func() {
			if r := recover(); r != nil {
				h.logger.Error("Panic in callback function",
					zap.String("correlation_id", correlationID),
					zap.Any("panic", r))
			}
		}()

		// Execute callback with timeout
		done := make(chan bool, 1)
		go func() {
			defer func() {
				if r := recover(); r != nil {
					h.logger.Error("Panic during callback execution",
						zap.String("correlation_id", correlationID),
						zap.Any("panic", r))
				}
				done <- true
			}()
			callback(reply)
		}()

		// Wait for callback completion or timeout
		select {
		case <-done:
			h.logger.Debug("Callback executed successfully",
				zap.String("correlation_id", correlationID))
		case <-time.After(h.config.DefaultTimeout):
			h.logger.Error("Callback execution timeout",
				zap.String("correlation_id", correlationID),
				zap.Duration("timeout", h.config.DefaultTimeout))
		}
	}()
}

// MessageReplyHandler handles incoming Kafka messages for replies
type MessageReplyHandler struct {
	replyHandler port.IAsyncReplyHandler
	logger       *zap.Logger
	config       *config.AsyncConfig
}

func NewMessageReplyHandler(
	replyHandler port.IAsyncReplyHandler,
	logger *zap.Logger,
	config *config.AsyncConfig,
) *MessageReplyHandler {
	return &MessageReplyHandler{
		replyHandler: replyHandler,
		logger:       logger,
		config:       config,
	}
}

func (h *MessageReplyHandler) HandleMessage(ctx context.Context, msg *msgEntity.KafkaMessage) error {
	asyncMsg, err := entity.FromJSON(msg.Value)
	if err != nil {
		h.logger.Error("Failed to parse async message", zap.Error(err))
		return err
	}

	if asyncMsg.Type != entity.MessageTypeReply {
		h.logger.Warn("Expected reply message, got",
			zap.String("type", string(asyncMsg.Type)),
			zap.String("correlation_id", asyncMsg.CorrelationID))
		return nil
	}

	// Convert to reply entity
	reply := &entity.AsyncReply{
		ID:            asyncMsg.ID,
		CorrelationID: asyncMsg.CorrelationID,
		Status:        asyncMsg.Status,
		Result:        asyncMsg.Payload,
		Error:         asyncMsg.Error,
		ProcessedAt:   asyncMsg.Timestamp,
	}

	return h.replyHandler.HandleReply(ctx, reply)
}

// AsyncSystemManager orchestrates the entire async request-reply system
type AsyncSystemManager struct {
	requestManager port.IAsyncRequestManager
	requestHandler port.IAsyncRequestHandler
	replyHandler   port.IAsyncReplyHandler
	
	// Message handlers
	requestMessageHandler *MessageRequestHandler
	replyMessageHandler   *MessageReplyHandler
	
	logger  *zap.Logger
	config  *config.AsyncConfig
	running bool
	mu      sync.RWMutex
}

func NewAsyncSystemManager(
	requestManager port.IAsyncRequestManager,
	requestHandler port.IAsyncRequestHandler,
	replyHandler port.IAsyncReplyHandler,
	requestMessageHandler *MessageRequestHandler,
	replyMessageHandler *MessageReplyHandler,
	logger *zap.Logger,
	config *config.AsyncConfig,
) *AsyncSystemManager {
	return &AsyncSystemManager{
		requestManager:        requestManager,
		requestHandler:        requestHandler,
		replyHandler:          replyHandler,
		requestMessageHandler: requestMessageHandler,
		replyMessageHandler:   replyMessageHandler,
		logger:                logger,
		config:                config,
	}
}

func (s *AsyncSystemManager) Start(ctx context.Context) error {
	s.mu.Lock()
	defer s.mu.Unlock()

	if s.running {
		return fmt.Errorf("async system manager is already running")
	}

	// Start request manager
	if err := s.requestManager.Start(ctx); err != nil {
		return fmt.Errorf("failed to start request manager: %w", err)
	}

	s.running = true
	s.logger.Info("AsyncSystemManager started")
	return nil
}

func (s *AsyncSystemManager) Stop(ctx context.Context) error {
	s.mu.Lock()
	defer s.mu.Unlock()

	if !s.running {
		return fmt.Errorf("async system manager is not running")
	}

	// Stop request manager
	if err := s.requestManager.Stop(ctx); err != nil {
		s.logger.Error("Failed to stop request manager", zap.Error(err))
	}

	s.running = false
	s.logger.Info("AsyncSystemManager stopped")
	return nil
}

func (s *AsyncSystemManager) IsRunning() bool {
	s.mu.RLock()
	defer s.mu.RUnlock()
	return s.running
}

func (s *AsyncSystemManager) GetRequestManager() port.IAsyncRequestManager {
	return s.requestManager
}

func (s *AsyncSystemManager) GetRequestHandler() port.IAsyncRequestHandler {
	return s.requestHandler
}

func (s *AsyncSystemManager) GetReplyHandler() port.IAsyncReplyHandler {
	return s.replyHandler
}

func (s *AsyncSystemManager) GetRequestMessageHandler() *MessageRequestHandler {
	return s.requestMessageHandler
}

func (s *AsyncSystemManager) GetReplyMessageHandler() *MessageReplyHandler {
	return s.replyMessageHandler
}
