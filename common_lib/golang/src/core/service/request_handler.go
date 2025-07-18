package service

import (
	"context"
	"fmt"
	"time"

	"github.com/google/uuid"
	"go.uber.org/zap"

	"github.com/quantuanhuy/lib/src/config"
	entity "github.com/quantuanhuy/lib/src/core/entity/async"
	msgEntity "github.com/quantuanhuy/lib/src/core/entity/msg"
	"github.com/quantuanhuy/lib/src/core/port"
)

// DefaultRequestProcessor provides a simple implementation for testing
type DefaultRequestProcessor struct {
	logger *zap.Logger
}

func NewDefaultRequestProcessor(logger *zap.Logger) entity.AsyncRequestHandler {
	return &DefaultRequestProcessor{
		logger: logger,
	}
}

func (p *DefaultRequestProcessor) Handle(request *entity.AsyncRequest) (*entity.AsyncReply, error) {
	// Simulate processing time
	time.Sleep(100 * time.Millisecond)

	p.logger.Info("Processing default async request",
		zap.String("correlation_id", request.CorrelationID),
		zap.String("request_type", request.RequestType))

	reply := &entity.AsyncReply{
		ID:            uuid.New().String(),
		CorrelationID: request.CorrelationID,
		RequestID:     request.ID,
		Status:        entity.AsyncRequestStatusCompleted,
		Result: map[string]interface{}{
			"processed":    true,
			"timestamp":    time.Now(),
			"request_type": request.RequestType,
			"message":      "Request processed successfully",
		},
		ProcessedAt: time.Now(),
		Duration:    100 * time.Millisecond,
	}

	return reply, nil
}

// Legacy handler for backward compatibility
type LegacyRequestMessageHandler struct {
	requestHandler port.IAsyncRequestHandler
	producer       port.IKafkaProducerPort
	logger         *zap.Logger
	config         *config.AsyncConfig
}

func NewLegacyRequestMessageHandler(
	requestHandler port.IAsyncRequestHandler,
	producer port.IKafkaProducerPort,
	logger *zap.Logger,
	config *config.AsyncConfig,
) *LegacyRequestMessageHandler {
	return &LegacyRequestMessageHandler{
		requestHandler: requestHandler,
		producer:       producer,
		logger:         logger,
		config:         config,
	}
}

func (h *LegacyRequestMessageHandler) HandleMessage(ctx context.Context, msg *msgEntity.KafkaMessage) error {
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

func (h *LegacyRequestMessageHandler) handleRequest(ctx context.Context, msg *entity.AsyncMessage) error {
	startTime := time.Now()

	// Convert to request entity
	request := &entity.AsyncRequest{
		ID:            uuid.New().String(),
		CorrelationID: msg.CorrelationID,
		RequestType:   msg.RequestType,
		Status:        entity.AsyncRequestStatusPending,
		Payload:       msg.Payload,
		Timeout:       msg.TTL,
		CreatedAt:     msg.Timestamp,
		UpdatedAt:     startTime,
	}

	// Process request
	reply, err := h.requestHandler.HandleRequest(ctx, request)

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
	h.logger.Info("Processed async request",
		zap.String("correlation_id", msg.CorrelationID),
		zap.String("request_type", msg.RequestType),
		zap.String("status", string(replyMsg.Status)),
		zap.Duration("duration", duration))

	return nil
}

func (h *LegacyRequestMessageHandler) handleCancel(ctx context.Context, msg *entity.AsyncMessage) error {
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

func (h *LegacyRequestMessageHandler) sendReply(ctx context.Context, replyTo string, replyMsg *entity.AsyncMessage) error {
	data, err := replyMsg.ToJSON()
	if err != nil {
		return fmt.Errorf("failed to marshal reply message: %w", err)
	}

	kafkaMsg := &msgEntity.KafkaMessage{
		Key:       []byte(replyMsg.CorrelationID),
		Topic:     replyTo,
		Value:     data,
		TimeStamp: time.Now().Unix(),
	}

	return h.producer.SendMessage(ctx, kafkaMsg)
}
