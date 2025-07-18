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

type AsyncRequestHandler struct {
	producer port.IKafkaProducerPort
	logger   *zap.Logger
	config   *config.AsyncConfig
	
	// Request handlers by type
	handlers map[string]entity.AsyncRequestHandler
	mutex    sync.RWMutex
}

func NewAsyncRequestHandler(
	producer port.IKafkaProducerPort,
	logger *zap.Logger,
	config *config.AsyncConfig,
) port.IAsyncRequestHandler {
	return &AsyncRequestHandler{
		producer: producer,
		logger:   logger,
		config:   config,
		handlers: make(map[string]entity.AsyncRequestHandler),
	}
}

func (h *AsyncRequestHandler) RegisterHandler(requestType string, handler entity.AsyncRequestHandler) {
	h.mutex.Lock()
	defer h.mutex.Unlock()
	
	h.handlers[requestType] = handler
	h.logger.Info("Registered async request handler",
		zap.String("request_type", requestType))
}

func (h *AsyncRequestHandler) UnregisterHandler(requestType string) {
	h.mutex.Lock()
	defer h.mutex.Unlock()
	
	delete(h.handlers, requestType)
	h.logger.Info("Unregistered async request handler",
		zap.String("request_type", requestType))
}

func (h *AsyncRequestHandler) HandleRequest(ctx context.Context, request *entity.AsyncRequest) (*entity.AsyncReply, error) {
	h.mutex.RLock()
	handler, exists := h.handlers[request.RequestType]
	h.mutex.RUnlock()

	if !exists {
		return nil, fmt.Errorf("no handler registered for request type: %s", request.RequestType)
	}

	// Mark request as processing
	request.MarkAsProcessing()

	h.logger.Info("Processing async request",
		zap.String("correlation_id", request.CorrelationID),
		zap.String("request_type", request.RequestType))

	// Execute handler
	reply, err := handler.Handle(request)
	if err != nil {
		request.MarkAsFailed(err.Error())
		h.logger.Error("Failed to process async request",
			zap.String("correlation_id", request.CorrelationID),
			zap.String("request_type", request.RequestType),
			zap.Error(err))
		return nil, err
	}

	request.MarkAsCompleted()
	
	h.logger.Info("Completed async request",
		zap.String("correlation_id", request.CorrelationID),
		zap.String("request_type", request.RequestType),
		zap.Duration("duration", reply.Duration))

	return reply, nil
}

// MessageRequestHandler handles incoming Kafka messages for requests
type MessageRequestHandler struct {
	requestHandler port.IAsyncRequestHandler
	producer       port.IKafkaProducerPort
	logger         *zap.Logger
	config         *config.AsyncConfig
}

func NewMessageRequestHandler(
	requestHandler port.IAsyncRequestHandler,
	producer port.IKafkaProducerPort,
	logger *zap.Logger,
	config *config.AsyncConfig,
) *MessageRequestHandler {
	return &MessageRequestHandler{
		requestHandler: requestHandler,
		producer:       producer,
		logger:         logger,
		config:         config,
	}
}

func (h *MessageRequestHandler) HandleMessage(ctx context.Context, msg *msgEntity.KafkaMessage) error {
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

func (h *MessageRequestHandler) handleRequest(ctx context.Context, msg *entity.AsyncMessage) error {
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

func (h *MessageRequestHandler) handleCancel(ctx context.Context, msg *entity.AsyncMessage) error {
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

func (h *MessageRequestHandler) sendReply(ctx context.Context, replyTo string, replyMsg *entity.AsyncMessage) error {
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
