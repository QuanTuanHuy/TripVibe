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
	kafkaentity "github.com/quantuanhuy/lib/src/core/entity/msg"
	"github.com/quantuanhuy/lib/src/core/port"
)

type AsyncRequestManager struct {
	producer           port.IKafkaProducerPort
	correlationManager port.ICorrelationPort
	callbackManager    port.ICallbackManager
	logger             *zap.Logger
	config             *config.AsyncConfig
	
	// Reply waiting channels
	replyChannels     map[string]chan *entity.AsyncReply
	replyChannelMutex sync.RWMutex
	
	// Lifecycle management
	running bool
	cancel  context.CancelFunc
	mu      sync.RWMutex
}

func NewAsyncRequestManager(
	producer port.IKafkaProducerPort,
	correlationManager port.ICorrelationPort,
	callbackManager port.ICallbackManager,
	logger *zap.Logger,
	config *config.AsyncConfig,
) port.IAsyncRequestManager {
	return &AsyncRequestManager{
		producer:           producer,
		correlationManager: correlationManager,
		callbackManager:    callbackManager,
		logger:             logger,
		config:             config,
		replyChannels:      make(map[string]chan *entity.AsyncReply),
	}
}

func (a *AsyncRequestManager) SendRequest(ctx context.Context, requestType string, payload map[string]interface{}, timeout time.Duration) (string, error) {
	return a.SendRequestWithCallback(ctx, requestType, payload, timeout, nil)
}

func (a *AsyncRequestManager) SendRequestWithCallback(ctx context.Context, requestType string, payload map[string]interface{}, timeout time.Duration, callback entity.AsyncCallback) (string, error) {
	if !a.IsRunning() {
		return "", fmt.Errorf("async request manager is not running")
	}

	correlationID := uuid.New().String()

	// Create correlation data
	correlationData := entity.NewCorrelationData(
		correlationID,
		requestType,
		a.config.ReplyTopic,
		timeout,
		payload,
	)

	// Store correlation data
	if err := a.correlationManager.StoreCorrelation(ctx, correlationID, correlationData); err != nil {
		return "", fmt.Errorf("failed to store correlation: %w", err)
	}

	// Store callback if provided
	if callback != nil {
		a.callbackManager.StoreCallback(correlationID, callback)
	}

	// Create and send message
	message := &entity.AsyncMessage{
		ID:            uuid.New().String(),
		Type:          entity.MessageTypeRequest,
		CorrelationID: correlationID,
		RequestType:   requestType,
		ReplyTo:       a.config.ReplyTopic,
		Payload:       payload,
		Timestamp:     time.Now(),
		TTL:           timeout,
	}

	if err := a.sendMessage(ctx, a.config.RequestTopic, message); err != nil {
		// Cleanup on failure
		a.correlationManager.RemoveCorrelation(ctx, correlationID)
		a.callbackManager.RemoveCallback(correlationID)
		return "", fmt.Errorf("failed to send request message: %w", err)
	}

	a.logger.Info("Sent async request",
		zap.String("correlation_id", correlationID),
		zap.String("request_type", requestType),
		zap.Duration("timeout", timeout))

	return correlationID, nil
}

func (a *AsyncRequestManager) WaitForReply(ctx context.Context, correlationID string, timeout time.Duration) (*entity.AsyncReply, error) {
	if !a.IsRunning() {
		return nil, fmt.Errorf("async request manager is not running")
	}

	replyChan := make(chan *entity.AsyncReply, 1)

	a.replyChannelMutex.Lock()
	a.replyChannels[correlationID] = replyChan
	a.replyChannelMutex.Unlock()

	defer func() {
		a.replyChannelMutex.Lock()
		delete(a.replyChannels, correlationID)
		a.replyChannelMutex.Unlock()
		close(replyChan)
	}()

	// Wait for reply or timeout
	select {
	case reply := <-replyChan:
		return reply, nil
	case <-time.After(timeout):
		// Cleanup on timeout
		a.correlationManager.RemoveCorrelation(ctx, correlationID)
		a.callbackManager.RemoveCallback(correlationID)
		return nil, fmt.Errorf("timeout waiting for reply for correlation ID: %s", correlationID)
	case <-ctx.Done():
		// Cleanup on context cancellation
		a.correlationManager.RemoveCorrelation(ctx, correlationID)
		a.callbackManager.RemoveCallback(correlationID)
		return nil, ctx.Err()
	}
}

func (a *AsyncRequestManager) CancelRequest(ctx context.Context, correlationID string) error {
	if !a.IsRunning() {
		return fmt.Errorf("async request manager is not running")
	}

	// Get correlation data to send cancel message
	correlationData, err := a.correlationManager.GetCorrelation(ctx, correlationID)
	if err != nil {
		return fmt.Errorf("failed to get correlation data: %w", err)
	}

	// Send cancel message
	cancelMessage := &entity.AsyncMessage{
		ID:            uuid.New().String(),
		Type:          entity.MessageTypeCancel,
		CorrelationID: correlationID,
		RequestType:   correlationData.RequestType,
		ReplyTo:       correlationData.ReplyTo,
		Timestamp:     time.Now(),
	}

	if err := a.sendMessage(ctx, a.config.RequestTopic, cancelMessage); err != nil {
		return fmt.Errorf("failed to send cancel message: %w", err)
	}

	a.logger.Info("Sent cancel request",
		zap.String("correlation_id", correlationID))

	return nil
}

func (a *AsyncRequestManager) GetRequestStatus(ctx context.Context, correlationID string) (entity.AsyncRequestStatus, error) {
	correlationData, err := a.correlationManager.GetCorrelation(ctx, correlationID)
	if err != nil {
		return "", fmt.Errorf("failed to get correlation data: %w", err)
	}

	return correlationData.Status, nil
}

func (a *AsyncRequestManager) Start(ctx context.Context) error {
	a.mu.Lock()
	defer a.mu.Unlock()

	if a.running {
		return fmt.Errorf("async request manager is already running")
	}

	processingCtx, cancel := context.WithCancel(ctx)
	a.cancel = cancel
	a.running = true

	// Start cleanup goroutine
	go a.cleanupExpiredCorrelations(processingCtx)

	a.logger.Info("AsyncRequestManager started")
	return nil
}

func (a *AsyncRequestManager) Stop(ctx context.Context) error {
	a.mu.Lock()
	defer a.mu.Unlock()

	if !a.running {
		return fmt.Errorf("async request manager is not running")
	}

	if a.cancel != nil {
		a.cancel()
	}

	a.running = false

	// Close all reply channels
	a.replyChannelMutex.Lock()
	for _, ch := range a.replyChannels {
		close(ch)
	}
	a.replyChannels = make(map[string]chan *entity.AsyncReply)
	a.replyChannelMutex.Unlock()

	a.logger.Info("AsyncRequestManager stopped")
	return nil
}

func (a *AsyncRequestManager) IsRunning() bool {
	a.mu.RLock()
	defer a.mu.RUnlock()
	return a.running
}

func (a *AsyncRequestManager) sendMessage(ctx context.Context, topic string, message *entity.AsyncMessage) error {
	data, err := message.ToJSON()
	if err != nil {
		return fmt.Errorf("failed to marshal message: %w", err)
	}

	kafkaMsg := &kafkaentity.KafkaMessage{
		Topic:     topic,
		Key:       []byte(message.CorrelationID),
		Value:     data,
		TimeStamp: time.Now().Unix(),
	}

	return a.producer.SendMessage(ctx, kafkaMsg)
}

func (a *AsyncRequestManager) cleanupExpiredCorrelations(ctx context.Context) {
	ticker := time.NewTicker(a.config.CleanupInterval)
	defer ticker.Stop()

	for {
		select {
		case <-ctx.Done():
			a.logger.Info("Stopping cleanup of expired correlations")
			return
		case <-ticker.C:
			if err := a.correlationManager.CleanupExpired(ctx); err != nil {
				a.logger.Error("Failed to cleanup expired correlations", zap.Error(err))
			} else {
				a.logger.Debug("Expired correlations cleaned up successfully")
			}
		}
	}
}

// NotifyReply is used by reply handlers to notify waiting requests
func (a *AsyncRequestManager) NotifyReply(correlationID string, reply *entity.AsyncReply) {
	a.replyChannelMutex.RLock()
	replyChan, exists := a.replyChannels[correlationID]
	a.replyChannelMutex.RUnlock()

	if exists {
		select {
		case replyChan <- reply:
			a.logger.Debug("Notified waiting request",
				zap.String("correlation_id", correlationID))
		default:
			a.logger.Warn("Failed to notify waiting request, channel full",
				zap.String("correlation_id", correlationID))
		}
	}
}
