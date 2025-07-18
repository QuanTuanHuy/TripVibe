package service

import (
	"context"
	"fmt"
	"time"

	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"

	"github.com/quantuanhuy/lib/src/config"
	"github.com/quantuanhuy/lib/src/core/port"
	"github.com/quantuanhuy/lib/src/infrastructure/async"
)

// AsyncSystemBootstrap provides factory methods for creating async components
type AsyncSystemBootstrap struct {
	logger         *zap.Logger
	config         *config.AsyncConfig
	redisClient    *redis.Client
	kafkaProducer  port.IKafkaProducerPort
}

func NewAsyncSystemBootstrap(
	logger *zap.Logger,
	config *config.AsyncConfig,
	redisClient *redis.Client,
	kafkaProducer port.IKafkaProducerPort,
) *AsyncSystemBootstrap {
	return &AsyncSystemBootstrap{
		logger:        logger,
		config:        config,
		redisClient:   redisClient,
		kafkaProducer: kafkaProducer,
	}
}

// CreateAsyncSystem creates and wires all async components
func (b *AsyncSystemBootstrap) CreateAsyncSystem() *AsyncSystemManager {
	// Create infrastructure components
	correlationMgr := async.NewRedisCorrelationManager(
		b.redisClient,
		b.logger,
		"async",
		b.config.CorrelationTTL,
	)

	callbackMgr := async.NewCallbackManager()

	// Create core services
	requestManager := NewAsyncRequestManager(
		b.kafkaProducer,
		correlationMgr,
		callbackMgr,
		b.logger,
		b.config,
	)

	requestHandler := NewAsyncRequestHandler(
		b.kafkaProducer,
		b.logger,
		b.config,
	)

	replyHandler := NewAsyncReplyHandler(
		correlationMgr,
		callbackMgr,
		b.logger,
		b.config,
	)

	// Create message handlers
	requestMessageHandler := NewMessageRequestHandler(
		requestHandler,
		b.kafkaProducer,
		b.logger,
		b.config,
	)

	replyMessageHandler := NewMessageReplyHandler(
		replyHandler,
		b.logger,
		b.config,
	)

	// Create system manager
	systemManager := NewAsyncSystemManager(
		requestManager,
		requestHandler,
		replyHandler,
		requestMessageHandler,
		replyMessageHandler,
		b.logger,
		b.config,
	)

	// Wire components together
	if asyncReplyHandler, ok := replyHandler.(*AsyncReplyHandler); ok {
		if asyncRequestManager, ok := requestManager.(*AsyncRequestManager); ok {
			asyncReplyHandler.SetRequestManager(asyncRequestManager)
		}
	}

	return systemManager
}

// RegisterDefaultHandlers registers some default request handlers for testing
func (b *AsyncSystemBootstrap) RegisterDefaultHandlers(requestHandler port.IAsyncRequestHandler) {
	// Register default handler for testing
	defaultHandler := NewDefaultRequestProcessor(b.logger)
	requestHandler.RegisterHandler("default", defaultHandler)
	requestHandler.RegisterHandler("test", defaultHandler)
	requestHandler.RegisterHandler("echo", defaultHandler)
}

// CreateStandaloneComponents creates individual components for custom wiring
func (b *AsyncSystemBootstrap) CreateStandaloneComponents() (
	port.ICorrelationPort,
	port.ICallbackManager,
	port.IAsyncRequestManager,
	port.IAsyncRequestHandler,
	port.IAsyncReplyHandler,
) {
	correlationMgr := async.NewRedisCorrelationManager(
		b.redisClient,
		b.logger,
		"async",
		b.config.CorrelationTTL,
	)

	callbackMgr := async.NewCallbackManager()

	requestManager := NewAsyncRequestManager(
		b.kafkaProducer,
		correlationMgr,
		callbackMgr,
		b.logger,
		b.config,
	)

	requestHandler := NewAsyncRequestHandler(
		b.kafkaProducer,
		b.logger,
		b.config,
	)

	replyHandler := NewAsyncReplyHandler(
		correlationMgr,
		callbackMgr,
		b.logger,
		b.config,
	)

	return correlationMgr, callbackMgr, requestManager, requestHandler, replyHandler
}

// CreateLegacyComponents creates legacy components for backward compatibility
func (b *AsyncSystemBootstrap) CreateLegacyComponents() (
	*ReplyMessageHandler,
	*LegacyRequestMessageHandler,
) {
	correlationMgr := async.NewRedisCorrelationManager(
		b.redisClient,
		b.logger,
		"async",
		b.config.CorrelationTTL,
	)

	callbackMgr := async.NewCallbackManager()

	replyHandler := NewReplyMessageHandler(
		correlationMgr,
		callbackMgr,
		b.logger,
		b.config,
	)

	requestHandler := NewAsyncRequestHandler(
		b.kafkaProducer,
		b.logger,
		b.config,
	)

	legacyRequestHandler := NewLegacyRequestMessageHandler(
		requestHandler,
		b.kafkaProducer,
		b.logger,
		b.config,
	)

	return replyHandler, legacyRequestHandler
}

// ValidateConfiguration validates the async configuration
func (b *AsyncSystemBootstrap) ValidateConfiguration() error {
	if b.config.RequestTopic == "" {
		return fmt.Errorf("request topic is required")
	}
	if b.config.ReplyTopic == "" {
		return fmt.Errorf("reply topic is required")
	}
	if b.config.DefaultTimeout <= 0 {
		return fmt.Errorf("default timeout must be greater than 0")
	}
	if b.config.CorrelationTTL <= 0 {
		return fmt.Errorf("correlation TTL must be greater than 0")
	}
	if b.config.CleanupInterval <= 0 {
		return fmt.Errorf("cleanup interval must be greater than 0")
	}
	return nil
}

// CreateDefaultConfiguration creates a default configuration for async system
func CreateDefaultAsyncConfig() *config.AsyncConfig {
	return &config.AsyncConfig{
		RequestTopic:        "async-requests",
		ReplyTopic:          "async-replies",
		DefaultTimeout:      30 * time.Second,
		RequestTimeout:      60 * time.Second,
		ReplyTimeout:        30 * time.Second,
		MaxConcurrency:      100,
		MaxRequestHandlers:  10,
		MaxReplyHandlers:    10,
		CleanupInterval:     5 * time.Minute,
		CorrelationTTL:      1 * time.Hour,
		ExpiredCleanupBatch: 100,
		RetryAttempts:       3,
		RetryDelay:          1 * time.Second,
		RetryBackoffRate:    2.0,
		MetricsEnabled:      true,
		MonitoringInterval:  1 * time.Minute,
	}
}

// HealthCheck performs health check on async system components
func (b *AsyncSystemBootstrap) HealthCheck(ctx context.Context) error {
	// Check Redis connection
	if err := b.redisClient.Ping(ctx).Err(); err != nil {
		return fmt.Errorf("redis health check failed: %w", err)
	}

	// Add more health checks as needed
	return nil
}
