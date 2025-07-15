package bootstrap

import (
	"context"
	"fmt"

	"github.com/quantuanhuy/lib/src/config"
	"github.com/quantuanhuy/lib/src/core/service"
	"github.com/redis/go-redis/v9"
	"go.uber.org/fx"
	"go.uber.org/zap"
)

// SchedulerModule provides dependency injection for scheduler components
var SchedulerModule = fx.Options(
	fx.Provide(
		NewRedisClient,
		NewSchedulerService,
		config.DefaultSchedulerConfig,
	),
)

// NewRedisClient creates a new Redis client
func NewRedisClient(cfg *config.SchedulerConfig) (*redis.Client, error) {
	client := redis.NewClient(&redis.Options{
		Addr:     fmt.Sprintf("%s:%d", cfg.Redis.Host, cfg.Redis.Port),
		Password: cfg.Redis.Password,
		DB:       cfg.Redis.DB,
	})

	// Test connection
	ctx := context.Background()
	if err := client.Ping(ctx).Err(); err != nil {
		return nil, fmt.Errorf("failed to connect to Redis: %w", err)
	}

	return client, nil
}

// NewSchedulerService creates a new scheduler service
func NewSchedulerService(redisClient *redis.Client, logger *zap.Logger, cfg *config.SchedulerConfig) (*service.SchedulerService, error) {
	return service.NewSchedulerService(redisClient, logger, cfg)
}

// SchedulerBootstrap provides a complete scheduler setup
type SchedulerBootstrap struct {
	schedulerService *service.SchedulerService
	logger           *zap.Logger
}

// NewSchedulerBootstrap creates a new scheduler bootstrap
func NewSchedulerBootstrap(schedulerService *service.SchedulerService, logger *zap.Logger) *SchedulerBootstrap {
	return &SchedulerBootstrap{
		schedulerService: schedulerService,
		logger:           logger,
	}
}

// Start starts the scheduler service
func (b *SchedulerBootstrap) Start(ctx context.Context) error {
	b.logger.Info("Starting scheduler bootstrap")
	return b.schedulerService.Start(ctx)
}

// Stop stops the scheduler service
func (b *SchedulerBootstrap) Stop(ctx context.Context) error {
	b.logger.Info("Stopping scheduler bootstrap")
	return b.schedulerService.Stop(ctx)
}

// GetSchedulerService returns the scheduler service
func (b *SchedulerBootstrap) GetSchedulerService() *service.SchedulerService {
	return b.schedulerService
}
