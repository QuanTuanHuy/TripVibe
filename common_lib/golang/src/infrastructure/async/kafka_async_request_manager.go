package async

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

// KafkaAsyncRequestManager implements async request-reply pattern using Kafka
type KafkaAsyncRequestManager struct {
	producer           port.IKafkaProducerPort
	correlationMgr     port.ICorrelationManagerPort
	logger             *zap.Logger
	config             *config.AsyncConfig
	handlers           map[string]entity.AsyncRequestHandler
	handlersMutex      sync.RWMutex
	replyChannels      map[string]chan *entity.AsyncReply
	replyChannelsMutex sync.RWMutex
	running            bool
	cancel             context.CancelFunc
}

// NewKafkaAsyncRequestManager creates a new Kafka async request manager
func NewKafkaAsyncRequestManager(
	producer port.IKafkaProducerPort,
	correlationMgr port.ICorrelationManagerPort,
	logger *zap.Logger,
	config *config.AsyncConfig,
) port.IAsyncRequestManagerPort {
	return &KafkaAsyncRequestManager{
		producer:       producer,
		correlationMgr: correlationMgr,
		logger:         logger,
		config:         config,
		handlers:       make(map[string]entity.AsyncRequestHandler),
		replyChannels:  make(map[string]chan *entity.AsyncReply),
	}
}

// SendRequest sends an async request and returns correlation ID
func (k *KafkaAsyncRequestManager) SendRequest(
	ctx context.Context,
	requestType string,
	payload map[string]interface{},
	timeout time.Duration,
) (string, error) {
	correlationID := uuid.New().String()

	// Create async request
	request := &entity.AsyncRequest{
		ID:            uuid.New().String(),
		CorrelationID: correlationID,
		RequestType:   requestType,
		Status:        entity.AsyncRequestStatusPending,
		Payload:       payload,
		ReplyTo:       k.config.ReplyTopic,
		Timeout:       timeout,
		CreatedAt:     time.Now(),
		UpdatedAt:     time.Now(),
	}

	// Store correlation
	if err := k.correlationMgr.StoreCorrelation(ctx, correlationID, request, nil); err != nil {
		return "", fmt.Errorf("failed to store correlation: %w", err)
	}

	// Create async message
	message := &entity.AsyncMessage{
		ID:            uuid.New().String(),
		Type:          entity.MessageTypeRequest,
		CorrelationID: correlationID,
		RequestType:   requestType,
		ReplyTo:       k.config.ReplyTopic,
		Payload:       payload,
		Timestamp:     time.Now(),
		TTL:           timeout,
	}

	// Send message
	if err := k.sendMessage(ctx, k.config.RequestTopic, message); err != nil {
		k.correlationMgr.RemoveCorrelation(ctx, correlationID)
		return "", fmt.Errorf("failed to send request: %w", err)
	}

	k.logger.Info("Sent async request",
		zap.String("correlation_id", correlationID),
		zap.String("request_type", requestType))

	return correlationID, nil
}

// SendRequestWithCallback sends an async request with callback
func (k *KafkaAsyncRequestManager) SendRequestWithCallback(
	ctx context.Context,
	requestType string,
	payload map[string]interface{},
	timeout time.Duration,
	callback entity.AsyncCallback,
) (string, error) {
	correlationID := uuid.New().String()

	// Create async request
	request := &entity.AsyncRequest{
		ID:            uuid.New().String(),
		CorrelationID: correlationID,
		RequestType:   requestType,
		Status:        entity.AsyncRequestStatusPending,
		Payload:       payload,
		ReplyTo:       k.config.ReplyTopic,
		Timeout:       timeout,
		CreatedAt:     time.Now(),
		UpdatedAt:     time.Now(),
	}

	// Store correlation with callback
	if err := k.correlationMgr.StoreCorrelation(ctx, correlationID, request, callback); err != nil {
		return "", fmt.Errorf("failed to store correlation: %w", err)
	}

	// Create async message
	message := &entity.AsyncMessage{
		ID:            uuid.New().String(),
		Type:          entity.MessageTypeRequest,
		CorrelationID: correlationID,
		RequestType:   requestType,
		ReplyTo:       k.config.ReplyTopic,
		Payload:       payload,
		Timestamp:     time.Now(),
		TTL:           timeout,
	}

	// Send message
	if err := k.sendMessage(ctx, k.config.RequestTopic, message); err != nil {
		k.correlationMgr.RemoveCorrelation(ctx, correlationID)
		return "", fmt.Errorf("failed to send request: %w", err)
	}

	k.logger.Info("Sent async request with callback",
		zap.String("correlation_id", correlationID),
		zap.String("request_type", requestType))

	return correlationID, nil
}

// WaitForReply waits for a reply with correlation ID
func (k *KafkaAsyncRequestManager) WaitForReply(
	ctx context.Context,
	correlationID string,
	timeout time.Duration,
) (*entity.AsyncReply, error) {
	// Create reply channel
	replyChan := make(chan *entity.AsyncReply, 1)

	k.replyChannelsMutex.Lock()
	k.replyChannels[correlationID] = replyChan
	k.replyChannelsMutex.Unlock()

	// Cleanup channel when done
	defer func() {
		k.replyChannelsMutex.Lock()
		delete(k.replyChannels, correlationID)
		k.replyChannelsMutex.Unlock()
		close(replyChan)
	}()

	// Wait for reply or timeout
	select {
	case reply := <-replyChan:
		return reply, nil
	case <-time.After(timeout):
		// Timeout - remove correlation
		k.correlationMgr.RemoveCorrelation(ctx, correlationID)
		return nil, fmt.Errorf("timeout waiting for reply after %v", timeout)
	case <-ctx.Done():
		// Context cancelled
		k.correlationMgr.RemoveCorrelation(ctx, correlationID)
		return nil, ctx.Err()
	}
}

// CancelRequest cancels a pending request
func (k *KafkaAsyncRequestManager) CancelRequest(
	ctx context.Context,
	correlationID string,
) error {
	// Send cancel message
	message := &entity.AsyncMessage{
		ID:            uuid.New().String(),
		Type:          entity.MessageTypeCancel,
		CorrelationID: correlationID,
		Timestamp:     time.Now(),
	}

	if err := k.sendMessage(ctx, k.config.RequestTopic, message); err != nil {
		return fmt.Errorf("failed to send cancel message: %w", err)
	}

	// Remove correlation
	if err := k.correlationMgr.RemoveCorrelation(ctx, correlationID); err != nil {
		k.logger.Warn("Failed to remove correlation during cancel",
			zap.String("correlation_id", correlationID),
			zap.Error(err))
	}

	k.logger.Info("Cancelled async request", zap.String("correlation_id", correlationID))
	return nil
}

// GetRequestStatus gets the status of a request
func (k *KafkaAsyncRequestManager) GetRequestStatus(
	ctx context.Context,
	correlationID string,
) (entity.AsyncRequestStatus, error) {
	request, _, err := k.correlationMgr.GetCorrelation(ctx, correlationID)
	if err != nil {
		return "", fmt.Errorf("failed to get correlation: %w", err)
	}

	return request.Status, nil
}

// RegisterHandler registers a handler for a request type
func (k *KafkaAsyncRequestManager) RegisterHandler(
	requestType string,
	handler entity.AsyncRequestHandler,
) {
	k.handlersMutex.Lock()
	defer k.handlersMutex.Unlock()

	k.handlers[requestType] = handler
	k.logger.Info("Registered async request handler", zap.String("request_type", requestType))
}

// UnregisterHandler unregisters a handler for a request type
func (k *KafkaAsyncRequestManager) UnregisterHandler(requestType string) {
	k.handlersMutex.Lock()
	defer k.handlersMutex.Unlock()

	delete(k.handlers, requestType)
	k.logger.Info("Unregistered async request handler", zap.String("request_type", requestType))
}

// StartProcessing starts processing async requests
func (k *KafkaAsyncRequestManager) StartProcessing(ctx context.Context) error {
	if k.running {
		return fmt.Errorf("already running")
	}

	processingCtx, cancel := context.WithCancel(ctx)
	k.cancel = cancel
	k.running = true

	// Start cleanup goroutine
	go k.cleanupExpiredCorrelations(processingCtx)

	k.logger.Info("Started async request processing")
	return nil
}

// StopProcessing stops processing async requests
func (k *KafkaAsyncRequestManager) StopProcessing(ctx context.Context) error {
	if !k.running {
		return fmt.Errorf("not running")
	}

	k.cancel()
	k.running = false

	k.logger.Info("Stopped async request processing")
	return nil
}

// ProcessRequest processes an incoming request message
func (k *KafkaAsyncRequestManager) ProcessRequest(
	ctx context.Context,
	message *entity.AsyncMessage,
) error {
	startTime := time.Now()

	k.logger.Info("Processing async request",
		zap.String("correlation_id", message.CorrelationID),
		zap.String("request_type", message.RequestType))

	// Get handler
	k.handlersMutex.RLock()
	handler, exists := k.handlers[message.RequestType]
	k.handlersMutex.RUnlock()

	if !exists {
		k.logger.Warn("No handler registered for request type",
			zap.String("request_type", message.RequestType))
		return k.sendErrorReply(ctx, message, "no handler registered")
	}

	// Create request entity
	request := &entity.AsyncRequest{
		ID:            uuid.New().String(),
		CorrelationID: message.CorrelationID,
		RequestType:   message.RequestType,
		Status:        entity.AsyncRequestStatusProcessing,
		Payload:       message.Payload,
		ReplyTo:       message.ReplyTo,
		CreatedAt:     message.Timestamp,
		UpdatedAt:     time.Now(),
	}

	// Process request
	reply, err := handler(request)
	if err != nil {
		k.logger.Error("Handler failed",
			zap.String("correlation_id", message.CorrelationID),
			zap.Error(err))
		return k.sendErrorReply(ctx, message, err.Error())
	}

	// Send reply
	replyMsg := &entity.AsyncMessage{
		ID:            uuid.New().String(),
		Type:          entity.MessageTypeReply,
		CorrelationID: message.CorrelationID,
		Status:        reply.Status,
		Payload:       reply.Result,
		Error:         reply.Error,
		Timestamp:     time.Now(),
	}

	if err := k.sendMessage(ctx, message.ReplyTo, replyMsg); err != nil {
		k.logger.Error("Failed to send reply",
			zap.String("correlation_id", message.CorrelationID),
			zap.Error(err))
		return err
	}

	duration := time.Since(startTime)
	k.logger.Info("Completed async request",
		zap.String("correlation_id", message.CorrelationID),
		zap.String("status", string(reply.Status)),
		zap.Duration("duration", duration))

	return nil
}

// ProcessReply processes an incoming reply message
func (k *KafkaAsyncRequestManager) ProcessReply(
	ctx context.Context,
	message *entity.AsyncMessage,
) error {
	k.logger.Info("Processing async reply",
		zap.String("correlation_id", message.CorrelationID),
		zap.String("status", string(message.Status)))

	// Get correlation data
	request, callback, err := k.correlationMgr.GetCorrelation(ctx, message.CorrelationID)
	if err != nil {
		k.logger.Warn("Correlation not found",
			zap.String("correlation_id", message.CorrelationID),
			zap.Error(err))
		return nil // Not an error, might be expired or already processed
	}

	// Create reply entity
	reply := &entity.AsyncReply{
		ID:            message.ID,
		CorrelationID: message.CorrelationID,
		RequestID:     request.ID,
		Status:        message.Status,
		Result:        message.Payload,
		Error:         message.Error,
		ProcessedAt:   message.Timestamp,
	}

	// Execute callback if present
	if callback != nil {
		go func() {
			defer func() {
				if r := recover(); r != nil {
					k.logger.Error("Callback panic",
						zap.String("correlation_id", message.CorrelationID),
						zap.Any("panic", r))
				}
			}()

			callback(reply)
		}()
	}

	// Send to waiting channel if present
	k.replyChannelsMutex.RLock()
	if replyChan, exists := k.replyChannels[message.CorrelationID]; exists {
		select {
		case replyChan <- reply:
		default:
			k.logger.Warn("Reply channel full", zap.String("correlation_id", message.CorrelationID))
		}
	}
	k.replyChannelsMutex.RUnlock()

	// Remove correlation
	if err := k.correlationMgr.RemoveCorrelation(ctx, message.CorrelationID); err != nil {
		k.logger.Warn("Failed to remove correlation",
			zap.String("correlation_id", message.CorrelationID),
			zap.Error(err))
	}

	return nil
}

// Helper methods
func (k *KafkaAsyncRequestManager) sendMessage(
	ctx context.Context,
	topic string,
	message *entity.AsyncMessage,
) error {
	data, err := message.ToJSON()
	if err != nil {
		return fmt.Errorf("failed to serialize message: %w", err)
	}

	// Import the correct message entity
	kafkaMsg := &msgEntity.KafkaMessage{
		Topic:     topic,
		Key:       []byte(message.CorrelationID),
		Value:     data,
		TimeStamp: time.Now().Unix(),
	}

	return k.producer.SendMessage(ctx, kafkaMsg)
}

func (k *KafkaAsyncRequestManager) sendErrorReply(
	ctx context.Context,
	originalMessage *entity.AsyncMessage,
	errorMsg string,
) error {
	replyMsg := &entity.AsyncMessage{
		ID:            uuid.New().String(),
		Type:          entity.MessageTypeReply,
		CorrelationID: originalMessage.CorrelationID,
		Status:        entity.AsyncRequestStatusFailed,
		Error:         errorMsg,
		Timestamp:     time.Now(),
	}

	return k.sendMessage(ctx, originalMessage.ReplyTo, replyMsg)
}

func (k *KafkaAsyncRequestManager) cleanupExpiredCorrelations(ctx context.Context) {
	ticker := time.NewTicker(k.config.CleanupInterval)
	defer ticker.Stop()

	for {
		select {
		case <-ctx.Done():
			return
		case <-ticker.C:
			if err := k.correlationMgr.CleanupExpired(ctx); err != nil {
				k.logger.Error("Failed to cleanup expired correlations", zap.Error(err))
			}
		}
	}
}
