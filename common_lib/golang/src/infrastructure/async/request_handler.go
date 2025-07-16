package async

import (
	"context"
	"time"

	"github.com/google/uuid"
	"go.uber.org/zap"

	"github.com/quantuanhuy/lib/src/config"
	entity "github.com/quantuanhuy/lib/src/core/entity/async"
	msgEntity "github.com/quantuanhuy/lib/src/core/entity/msg"
	"github.com/quantuanhuy/lib/src/core/port"
)

// RequestMessageHandler handles incoming request messages
type RequestMessageHandler struct {
	asyncManager port.IAsyncRequestManagerPort
	producer     port.IKafkaProducerPort
	logger       *zap.Logger
	config       *config.AsyncConfig
}

// NewRequestMessageHandler creates a new request message handler
func NewRequestMessageHandler(
	asyncManager port.IAsyncRequestManagerPort,
	producer port.IKafkaProducerPort,
	logger *zap.Logger,
	config *config.AsyncConfig,
) *RequestMessageHandler {
	return &RequestMessageHandler{
		asyncManager: asyncManager,
		producer:     producer,
		logger:       logger,
		config:       config,
	}
}

// HandleMessage handles incoming Kafka messages
func (h *RequestMessageHandler) HandleMessage(ctx context.Context, msg *msgEntity.KafkaMessage) error {
	// Parse async message
	asyncMsg, err := entity.FromJSON(msg.Value)
	if err != nil {
		h.logger.Error("Failed to parse async message", zap.Error(err))
		return err
	}

	switch asyncMsg.Type {
	case entity.MessageTypeRequest:
		return h.handleRequest(ctx, asyncMsg)
	case entity.MessageTypeCancel:
		return h.handleCancel(ctx, asyncMsg)
	default:
		h.logger.Warn("Unknown message type", zap.String("type", string(asyncMsg.Type)))
		return nil
	}
}

// handleRequest handles request messages
func (h *RequestMessageHandler) handleRequest(ctx context.Context, msg *entity.AsyncMessage) error {
	startTime := time.Now()

	h.logger.Info("Processing async request",
		zap.String("correlation_id", msg.CorrelationID),
		zap.String("request_type", msg.RequestType))

	// Create async request entity
	request := &entity.AsyncRequest{
		ID:            uuid.New().String(),
		CorrelationID: msg.CorrelationID,
		RequestType:   msg.RequestType,
		Status:        entity.AsyncRequestStatusProcessing,
		Payload:       msg.Payload,
		ReplyTo:       msg.ReplyTo,
		CreatedAt:     msg.Timestamp,
		UpdatedAt:     time.Now(),
	}

	// Process request
	reply, err := h.processRequest(ctx, request)

	// Create reply message
	replyMsg := &entity.AsyncMessage{
		ID:            uuid.New().String(),
		Type:          entity.MessageTypeReply,
		CorrelationID: msg.CorrelationID,
		Timestamp:     time.Now(),
	}

	if err != nil {
		replyMsg.Status = entity.AsyncRequestStatusFailed
		replyMsg.Error = err.Error()
	} else {
		replyMsg.Status = entity.AsyncRequestStatusCompleted
		replyMsg.Payload = reply.Result
	}

	// Send reply
	if err := h.sendReply(ctx, msg.ReplyTo, replyMsg); err != nil {
		h.logger.Error("Failed to send reply",
			zap.String("correlation_id", msg.CorrelationID),
			zap.Error(err))
		return err
	}

	duration := time.Since(startTime)
	h.logger.Info("Completed async request",
		zap.String("correlation_id", msg.CorrelationID),
		zap.String("status", string(replyMsg.Status)),
		zap.Duration("duration", duration))

	return nil
}

// handleCancel handles cancel messages
func (h *RequestMessageHandler) handleCancel(ctx context.Context, msg *entity.AsyncMessage) error {
	h.logger.Info("Processing cancel request",
		zap.String("correlation_id", msg.CorrelationID))

	// Create cancel reply
	replyMsg := &entity.AsyncMessage{
		ID:            uuid.New().String(),
		Type:          entity.MessageTypeReply,
		CorrelationID: msg.CorrelationID,
		Status:        entity.AsyncRequestStatusCancelled,
		Timestamp:     time.Now(),
	}

	return h.sendReply(ctx, msg.ReplyTo, replyMsg)
}

// processRequest simulates request processing
func (h *RequestMessageHandler) processRequest(ctx context.Context, request *entity.AsyncRequest) (*entity.AsyncReply, error) {
	// Simulate processing time
	time.Sleep(100 * time.Millisecond)

	reply := &entity.AsyncReply{
		ID:            uuid.New().String(),
		CorrelationID: request.CorrelationID,
		RequestID:     request.ID,
		Status:        entity.AsyncRequestStatusCompleted,
		Result: map[string]interface{}{
			"processed":    true,
			"timestamp":    time.Now(),
			"request_type": request.RequestType,
		},
		ProcessedAt: time.Now(),
		Duration:    100 * time.Millisecond,
	}

	return reply, nil
}

// sendReply sends a reply message
func (h *RequestMessageHandler) sendReply(ctx context.Context, replyTo string, replyMsg *entity.AsyncMessage) error {
	data, err := replyMsg.ToJSON()
	if err != nil {
		return err
	}

	kafkaMsg := &msgEntity.KafkaMessage{
		Topic:     replyTo,
		Key:       []byte(replyMsg.CorrelationID),
		Value:     data,
		TimeStamp: time.Now().Unix(),
	}

	return h.producer.SendMessage(ctx, kafkaMsg)
}
