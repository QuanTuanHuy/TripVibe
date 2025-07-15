package service

import (
	"context"
	"fmt"

	"github.com/quantuanhuy/lib/src/config"
	"github.com/quantuanhuy/lib/src/core/port"
	"github.com/quantuanhuy/lib/src/infrastructure/batch"
	"github.com/quantuanhuy/lib/src/infrastructure/scheduler"
	"github.com/quantuanhuy/lib/src/infrastructure/task"
	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"
)

// SchedulerService provides high-level scheduling functionality
type SchedulerService struct {
	jobScheduler   port.JobScheduler
	taskManager    port.TaskManager
	batchProcessor port.BatchProcessor
	logger         *zap.Logger
}

// NewSchedulerService creates a new scheduler service
func NewSchedulerService(redisClient *redis.Client, logger *zap.Logger, cfg *config.SchedulerConfig) (*SchedulerService, error) {
	// Create repositories
	jobRepo := scheduler.NewRedisJobRepository(redisClient, logger)
	taskRepo := task.NewRedisTaskRepository(redisClient, logger)
	batchRepo := batch.NewRedisBatchJobRepository(redisClient, logger)

	// Create job scheduler
	jobScheduler := scheduler.NewCronJobScheduler(jobRepo, logger, &cfg.Schedule)

	// Create task queue and worker
	taskQueue := task.NewRedisTaskQueue(redisClient, logger, "default_task_queue", 10000)
	taskWorker := task.NewTaskWorker("worker-1", taskQueue, taskRepo, logger, &cfg.Worker)
	taskManager := task.NewTaskManager(taskQueue, taskRepo, taskWorker, logger)

	// Create batch processor
	batchProcessor := batch.NewBatchProcessor(batchRepo, logger)

	return &SchedulerService{
		jobScheduler:   jobScheduler,
		taskManager:    taskManager,
		batchProcessor: batchProcessor,
		logger:         logger,
	}, nil
}

// GetJobScheduler returns the job scheduler
func (s *SchedulerService) GetJobScheduler() port.JobScheduler {
	return s.jobScheduler
}

// GetTaskManager returns the task manager
func (s *SchedulerService) GetTaskManager() port.TaskManager {
	return s.taskManager
}

// GetBatchProcessor returns the batch processor
func (s *SchedulerService) GetBatchProcessor() port.BatchProcessor {
	return s.batchProcessor
}

// Start starts all components
func (s *SchedulerService) Start(ctx context.Context) error {
	s.logger.Info("Starting scheduler service")

	// Start job scheduler
	if err := s.jobScheduler.Start(ctx); err != nil {
		return fmt.Errorf("failed to start job scheduler: %w", err)
	}

	// Start task manager
	if err := s.taskManager.Start(ctx); err != nil {
		return fmt.Errorf("failed to start task manager: %w", err)
	}

	// Start batch processor
	if err := s.batchProcessor.Start(ctx); err != nil {
		return fmt.Errorf("failed to start batch processor: %w", err)
	}

	s.logger.Info("Scheduler service started successfully")
	return nil
}

// Stop stops all components
func (s *SchedulerService) Stop(ctx context.Context) error {
	s.logger.Info("Stopping scheduler service")

	// Stop components in reverse order
	if err := s.batchProcessor.Stop(ctx); err != nil {
		s.logger.Error("Failed to stop batch processor", zap.Error(err))
	}

	if err := s.taskManager.Stop(ctx); err != nil {
		s.logger.Error("Failed to stop task manager", zap.Error(err))
	}

	if err := s.jobScheduler.Stop(ctx); err != nil {
		s.logger.Error("Failed to stop job scheduler", zap.Error(err))
	}

	s.logger.Info("Scheduler service stopped")
	return nil
}
