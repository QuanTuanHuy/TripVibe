package async

import (
	"context"
	"time"

	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"

	"github.com/quantuanhuy/lib/src/config"
	"github.com/quantuanhuy/lib/src/core/port"
)

// AsyncFactory creates and manages async components
type AsyncFactory struct {
	logger *zap.Logger
	config *config.AsyncConfig

	// Services
	redisClient *redis.Client
	producer    port.IKafkaProducerPort

	// Managers
	correlationMgr port.ICorrelationManagerPort
	asyncMgr       port.IAsyncRequestManagerPort

	// Handlers
	requestHandler *RequestMessageHandler
	replyHandler   *ReplyMessageHandler
}

// NewAsyncFactory creates a new async factory
func NewAsyncFactory(
	logger *zap.Logger,
	config *config.AsyncConfig,
	redisClient *redis.Client,
	producer port.IKafkaProducerPort,
) *AsyncFactory {
	return &AsyncFactory{
		logger:      logger,
		config:      config,
		redisClient: redisClient,
		producer:    producer,
	}
}

// CreateCorrelationManager creates a new correlation manager
func (f *AsyncFactory) CreateCorrelationManager() port.ICorrelationManagerPort {
	if f.correlationMgr == nil {
		f.correlationMgr = NewRedisCorrelationManager(
			f.redisClient,
			f.logger,
			"async:correlation:",
			f.config.CorrelationTTL, // Use CorrelationTTL instead of RequestTimeout
		)
	}
	return f.correlationMgr
}

// CreateAsyncRequestManager creates a new async request manager
func (f *AsyncFactory) CreateAsyncRequestManager() port.IAsyncRequestManagerPort {
	if f.asyncMgr == nil {
		correlationMgr := f.CreateCorrelationManager()
		f.asyncMgr = NewKafkaAsyncRequestManager(
			f.producer,
			correlationMgr,
			f.logger,
			f.config,
		)
	}
	return f.asyncMgr
}

// CreateRequestHandler creates a new request message handler
func (f *AsyncFactory) CreateRequestHandler() *RequestMessageHandler {
	if f.requestHandler == nil {
		asyncMgr := f.CreateAsyncRequestManager()
		f.requestHandler = NewRequestMessageHandler(
			asyncMgr,
			f.producer,
			f.logger,
			f.config,
		)
	}
	return f.requestHandler
}

// CreateReplyHandler creates a new reply message handler
func (f *AsyncFactory) CreateReplyHandler() *ReplyMessageHandler {
	if f.replyHandler == nil {
		asyncMgr := f.CreateAsyncRequestManager()
		correlationMgr := f.CreateCorrelationManager()
		f.replyHandler = NewReplyMessageHandler(
			asyncMgr,
			correlationMgr,
			f.logger,
			f.config,
		)
	}
	return f.replyHandler
}

// StartCleanupWorker starts a background worker to cleanup expired correlations
func (f *AsyncFactory) StartCleanupWorker(ctx context.Context) {
	correlationMgr := f.CreateCorrelationManager()

	ticker := time.NewTicker(f.config.CleanupInterval)
	defer ticker.Stop()

	f.logger.Info("Starting correlation cleanup worker",
		zap.Duration("interval", f.config.CleanupInterval))

	for {
		select {
		case <-ctx.Done():
			f.logger.Info("Stopping correlation cleanup worker")
			return
		case <-ticker.C:
			if err := correlationMgr.CleanupExpired(ctx); err != nil {
				f.logger.Error("Failed to cleanup expired correlations", zap.Error(err))
			}
		}
	}
}

// GetMetrics returns metrics for all components
func (f *AsyncFactory) GetMetrics() map[string]interface{} {
	ctx := context.Background()
	metrics := make(map[string]interface{})

	if f.correlationMgr != nil {
		pendingCount, _ := f.correlationMgr.GetPendingCount(ctx)
		metrics["correlation_manager"] = map[string]interface{}{
			"active":           true,
			"pending_count":    pendingCount,
			"cleanup_interval": f.config.CleanupInterval.String(),
		}
	}

	if f.asyncMgr != nil {
		metrics["async_manager"] = map[string]interface{}{
			"active": true,
			"config": map[string]interface{}{
				"request_topic":   f.config.RequestTopic,
				"reply_topic":     f.config.ReplyTopic,
				"default_timeout": f.config.DefaultTimeout.String(),
				"max_concurrency": f.config.MaxConcurrency,
				"retry_attempts":  f.config.RetryAttempts,
			},
		}
	}

	if f.requestHandler != nil {
		metrics["request_handler"] = map[string]interface{}{
			"active":               true,
			"max_request_handlers": f.config.MaxRequestHandlers,
		}
	}

	if f.replyHandler != nil {
		metrics["reply_handler"] = map[string]interface{}{
			"active":             true,
			"max_reply_handlers": f.config.MaxReplyHandlers,
		}
	}

	return metrics
}

// Shutdown gracefully shuts down all components
func (f *AsyncFactory) Shutdown(ctx context.Context) error {
	f.logger.Info("Shutting down async factory")

	var errs []error

	// Shutdown components in reverse order
	if f.replyHandler != nil {
		// Cleanup any pending replies
		if err := f.replyHandler.CleanupExpiredReplies(ctx); err != nil {
			errs = append(errs, err)
		}
	}

	if f.correlationMgr != nil {
		// Cleanup expired correlations
		if err := f.correlationMgr.CleanupExpired(ctx); err != nil {
			errs = append(errs, err)
		}
	}

	if len(errs) > 0 {
		f.logger.Error("Errors during shutdown", zap.Int("count", len(errs)))
		return errs[0] // Return first error
	}

	f.logger.Info("Async factory shutdown completed")
	return nil
}
