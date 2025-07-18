package service

import (
	"context"
	"time"

	"go.uber.org/zap"

	"github.com/quantuanhuy/lib/src/config"
	entity "github.com/quantuanhuy/lib/src/core/entity/async"
	msgEntity "github.com/quantuanhuy/lib/src/core/entity/msg"
	"github.com/quantuanhuy/lib/src/core/port"
)

type ReplyMessageHandler struct {
	correlationMgr port.ICorrelationPort
	callbackMgr    port.ICallbackManager
	logger         *zap.Logger
	config         *config.AsyncConfig
}

func NewReplyMessageHandler(
	correlationMgr port.ICorrelationPort,
	callbackMgr port.ICallbackManager,
	logger *zap.Logger,
	config *config.AsyncConfig,
) *ReplyMessageHandler {
	return &ReplyMessageHandler{
		correlationMgr: correlationMgr,
		callbackMgr:    callbackMgr,
		logger:         logger,
		config:         config,
	}
}

func (h *ReplyMessageHandler) HandleMessage(ctx context.Context, msg *msgEntity.KafkaMessage) error {
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

	return h.handleReply(ctx, asyncMsg)
}

func (h *ReplyMessageHandler) handleReply(ctx context.Context, msg *entity.AsyncMessage) error {
	h.logger.Info("Processing async reply",
		zap.String("correlation_id", msg.CorrelationID),
		zap.String("status", string(msg.Status)))

	// Get correlation data
	correlationData, err := h.correlationMgr.GetCorrelation(ctx, msg.CorrelationID)
	if err != nil {
		h.logger.Error("Failed to get correlation data",
			zap.String("correlation_id", msg.CorrelationID),
			zap.Error(err))
		return err
	}

	if correlationData == nil {
		h.logger.Warn("No correlation data found",
			zap.String("correlation_id", msg.CorrelationID))
		return nil
	}

	// Create reply entity
	reply := &entity.AsyncReply{
		ID:            msg.ID,
		CorrelationID: msg.CorrelationID,
		RequestID:     correlationData.ID,
		Status:        msg.Status,
		Result:        msg.Payload,
		Error:         msg.Error,
		ProcessedAt:   msg.Timestamp,
		Duration:      time.Since(correlationData.CreatedAt),
	}

	// Update correlation data with proper transaction-like behavior
	correlationData.UpdateStatus(msg.Status)
	
	// Store updated correlation data
	if err := h.correlationMgr.UpdateCorrelation(ctx, msg.CorrelationID, correlationData); err != nil {
		h.logger.Error("Failed to update correlation data",
			zap.String("correlation_id", msg.CorrelationID),
			zap.Error(err))
		// Continue processing even if update fails
	}

	// Handle callback if present
	if callback, exists := h.callbackMgr.GetCallback(msg.CorrelationID); exists {
		h.executeCallback(msg.CorrelationID, callback, reply)
	}

	h.logger.Info("Completed async reply",
		zap.String("correlation_id", msg.CorrelationID),
		zap.String("status", string(msg.Status)),
		zap.Duration("duration", reply.Duration))

	// Clean up correlation data after processing
	if err := h.correlationMgr.RemoveCorrelation(ctx, msg.CorrelationID); err != nil {
		h.logger.Error("Failed to remove correlation data",
			zap.String("correlation_id", msg.CorrelationID),
			zap.Error(err))
	}

	// Clean up callback
	h.callbackMgr.RemoveCallback(msg.CorrelationID)

	return nil
}

func (h *ReplyMessageHandler) executeCallback(correlationID string, callback entity.AsyncCallback, reply *entity.AsyncReply) {
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

// GetPendingReplies returns pending replies for debugging
func (h *ReplyMessageHandler) GetPendingReplies(ctx context.Context) ([]string, error) {
	pendingData, err := h.correlationMgr.GetAllPending(ctx)
	if err != nil {
		return nil, err
	}

	var pendingIDs []string
	for correlationID := range pendingData {
		pendingIDs = append(pendingIDs, correlationID)
	}

	return pendingIDs, nil
}
