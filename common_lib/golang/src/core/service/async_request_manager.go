package service

import (
	"context"
	"fmt"
	"sync"
	"time"

	"github.com/google/uuid"
	"github.com/quantuanhuy/lib/src/config"
	entity "github.com/quantuanhuy/lib/src/core/entity/async"
	kafkaentity "github.com/quantuanhuy/lib/src/core/entity/msg"
	"github.com/quantuanhuy/lib/src/core/port"
	"go.uber.org/zap"
)

type IAsyncRequestManager interface {
	SendRequest(ctx context.Context, requestType string, payload map[string]interface{}, timeout time.Duration) (string, error)

	SendRequestWithCallback(ctx context.Context, requestType string, payload map[string]interface{}, timeout time.Duration, callback entity.AsyncCallback) (string, error)

	WaitForReply(ctx context.Context, correlationID string, timeout time.Duration) (*entity.AsyncReply, error)

	CancelRequest(ctx context.Context, correlationID string) error

	StartProcessing(ctx context.Context) error

	StopProcessing(ctx context.Context) error
}

type AsyncRequestManager struct {
	producer           port.IKafkaProducerPort
	correlationManager port.ICorrelationPort
	logger             *zap.Logger
	config             *config.AsyncConfig
	handlers           map[string]entity.AsyncRequestHandler
	replyChannel       map[string]chan *entity.AsyncReply
	replyChannelMutex  map[string]*sync.Mutex
	running            bool
	cancel             context.CancelFunc
}

func (a *AsyncRequestManager) SendRequest(ctx context.Context, requestType string, payload map[string]interface{}, timeout time.Duration) (string, error) {
	correlationID := uuid.New().String()

	request := &entity.AsyncRequest{
		ID:            uuid.New().String(),
		CorrelationID: correlationID,
		RequestType:   requestType,
		Status:        entity.AsyncRequestStatusPending,
		Payload:       payload,
		Timeout:       timeout,
		CreatedAt:     time.Now(),
		UpdatedAt:     time.Now(),
	}

	if err := a.correlationManager.StoreCorrelation(ctx, correlationID, request, nil); err != nil {
		return "", fmt.Errorf("failed to store correlation: %w", err)
	}

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
		a.correlationManager.RemoveCorrelation(ctx, correlationID)
		return "", fmt.Errorf("failed to send request message: %w", err)
	}

	a.logger.Info("Async request sent",
		zap.String("correlation_id", correlationID),
		zap.String("request_type", requestType))

	return correlationID, nil
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

func (a *AsyncRequestManager) SendRequestWithCallback(ctx context.Context, requestType string, payload map[string]interface{}, timeout time.Duration, callback entity.AsyncCallback) (string, error) {
	correlationID := uuid.New().String()

	request := &entity.AsyncRequest{
		ID:            uuid.New().String(),
		CorrelationID: correlationID,
		RequestType:   requestType,
		Status:        entity.AsyncRequestStatusPending,
		Payload:       payload,
		Timeout:       timeout,
		CreatedAt:     time.Now(),
		UpdatedAt:     time.Now(),
	}

	if err := a.correlationManager.StoreCorrelation(ctx, correlationID, request, callback); err != nil {
		return "", fmt.Errorf("failed to store correlation: %w", err)
	}

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
		a.correlationManager.RemoveCorrelation(ctx, correlationID)
		return "", fmt.Errorf("failed to send request message: %w", err)
	}

	return correlationID, nil
}
func (a *AsyncRequestManager) StopProcessing(ctx context.Context) error {
	panic("unimplemented")
}

func (a *AsyncRequestManager) WaitForReply(ctx context.Context, correlationID string, timeout time.Duration) (*entity.AsyncReply, error) {
	panic("unimplemented")
}

func (a *AsyncRequestManager) CancelRequest(ctx context.Context, correlationID string) error {
	panic("unimplemented")
}

func (a *AsyncRequestManager) StartProcessing(ctx context.Context) error {
	if a.running {
		return fmt.Errorf("async request manager is already running")
	}

	processingCtx, cancel := context.WithCancel(ctx)
	a.cancel = cancel
	a.running = true

	go a.cleanupExpiredCorrelations(processingCtx)

	a.logger.Info("AsyncRequestManager started processing")
	return nil
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
			// if err := a.correlationManager.CleanupExpired(ctx); err != nil {
			// 	a.logger.Error("Failed to cleanup expired correlations", zap.Error(err))
			// } else {
			// 	a.logger.Info("Expired correlations cleaned up successfully")
			// }
		}
	}
}

func NewAsyncRequestManager(
	producer port.IKafkaProducerPort,
	correlationManager port.ICorrelationPort,
	logger *zap.Logger,
	config *config.AsyncConfig,
) IAsyncRequestManager {
	return &AsyncRequestManager{
		producer:           producer,
		correlationManager: correlationManager,
		logger:             logger,
		config:             config,
		handlers:           make(map[string]entity.AsyncRequestHandler),
		replyChannel:       make(map[string]chan *entity.AsyncReply),
		replyChannelMutex:  make(map[string]*sync.Mutex),
	}
}
