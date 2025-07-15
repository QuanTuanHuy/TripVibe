package scheduler

import (
	"context"
	"fmt"
	"sync"
	"time"

	"github.com/quantuanhuy/lib/src/core/entity/scheduler"
	"github.com/quantuanhuy/lib/src/core/port"
	"github.com/robfig/cron/v3"
	"go.uber.org/zap"
)

type CronJobScheduler struct {
	cron       *cron.Cron
	repository port.JobRepository
	logger     *zap.Logger
	handlers   map[string]scheduler.JobHandler
	config     *scheduler.ScheduleConfig
	mu         sync.RWMutex
	running    bool
	stopCh     chan struct{}
}

func NewCronJobScheduler(repository port.JobRepository, logger *zap.Logger, config *scheduler.ScheduleConfig) port.JobScheduler {
	return &CronJobScheduler{
		cron:       cron.New(cron.WithSeconds()),
		repository: repository,
		logger:     logger,
		handlers:   make(map[string]scheduler.JobHandler),
		config:     config,
		stopCh:     make(chan struct{}),
	}
}

func (s *CronJobScheduler) RegisterHandler(handler scheduler.JobHandler) error {
	s.mu.Lock()
	defer s.mu.Unlock()

	s.handlers[handler.GetType()] = handler
	s.logger.Info("Registered job handler", zap.String("type", handler.GetType()))
	return nil
}

func (s *CronJobScheduler) ScheduleJob(ctx context.Context, job *scheduler.Job) error {
	job.Status = scheduler.JobStatusPending
	job.CreatedAt = time.Now()
	job.UpdatedAt = time.Now()

	if err := s.repository.Save(ctx, job); err != nil {
		return fmt.Errorf("failed to save job: %w", err)
	}

	s.logger.Info("Job scheduled", zap.String("id", job.ID), zap.String("type", job.Type))
	return nil
}

func (s *CronJobScheduler) ScheduleCronJob(ctx context.Context, job *scheduler.Job) error {
	if job.CronExpr == "" {
		return fmt.Errorf("cron expression is required for cron job")
	}

	_, err := s.cron.AddFunc(job.CronExpr, func() {
		s.executeJob(ctx, job)
	})
	if err != nil {
		return fmt.Errorf("failed to schedule cron job: %w", err)
	}

	job.Status = scheduler.JobStatusPending
	job.CreatedAt = time.Now()
	job.UpdatedAt = time.Now()

	if err := s.repository.Save(ctx, job); err != nil {
		return fmt.Errorf("failed to save cron job: %w", err)
	}

	s.logger.Info("Cron job scheduled", zap.String("id", job.ID), zap.String("cron", job.CronExpr))
	return nil
}

func (s *CronJobScheduler) CancelJob(ctx context.Context, jobID string) error {
	job, err := s.repository.GetByID(ctx, jobID)
	if err != nil {
		return fmt.Errorf("failed to get job: %w", err)
	}

	job.Status = scheduler.JobStatusCancelled
	job.UpdatedAt = time.Now()

	if err := s.repository.Update(ctx, job); err != nil {
		return fmt.Errorf("failed to update job status: %w", err)
	}

	s.logger.Info("Job cancelled", zap.String("id", jobID))
	return nil
}

func (s *CronJobScheduler) Start(ctx context.Context) error {
	s.mu.Lock()
	defer s.mu.Unlock()

	if s.running {
		return fmt.Errorf("scheduler is already running")
	}

	s.running = true
	s.cron.Start()

	// Start the job processing loop
	go s.processJobs(ctx)

	s.logger.Info("Job scheduler started")
	return nil
}

func (s *CronJobScheduler) Stop(ctx context.Context) error {
	s.mu.Lock()
	defer s.mu.Unlock()

	if !s.running {
		return fmt.Errorf("scheduler is not running")
	}

	s.running = false
	s.cron.Stop()
	close(s.stopCh)

	s.logger.Info("Job scheduler stopped")
	return nil
}

func (s *CronJobScheduler) GetJobStatus(ctx context.Context, jobID string) (*scheduler.Job, error) {
	return s.repository.GetByID(ctx, jobID)
}

func (s *CronJobScheduler) GetRunningJobs(ctx context.Context) ([]*scheduler.Job, error) {
	return s.repository.GetByStatus(ctx, scheduler.JobStatusRunning)
}

func (s *CronJobScheduler) processJobs(ctx context.Context) {
	ticker := time.NewTicker(time.Second * 1) // Check every 1 second for tests
	defer ticker.Stop()

	for {
		select {
		case <-ctx.Done():
			return
		case <-s.stopCh:
			return
		case <-ticker.C:
			s.processScheduledJobs(ctx)
		}
	}
}

func (s *CronJobScheduler) processScheduledJobs(ctx context.Context) {
	jobs, err := s.repository.GetJobsToRun(ctx, time.Now())
	if err != nil {
		s.logger.Error("Failed to get jobs to run", zap.Error(err))
		return
	}

	for _, job := range jobs {
		if job.Status == scheduler.JobStatusPending {
			go s.executeJob(ctx, job)
		}
	}
}

func (s *CronJobScheduler) executeJob(ctx context.Context, job *scheduler.Job) {
	s.mu.RLock()
	handler, exists := s.handlers[job.Type]
	s.mu.RUnlock()

	if !exists {
		s.logger.Error("No handler found for job type", zap.String("type", job.Type))
		job.Status = scheduler.JobStatusFailed
		job.Error = fmt.Sprintf("No handler found for job type: %s", job.Type)
		job.UpdatedAt = time.Now()
		s.repository.Update(ctx, job)
		return
	}

	// Update job status to running
	job.Status = scheduler.JobStatusRunning
	job.UpdatedAt = time.Now()
	if err := s.repository.Update(ctx, job); err != nil {
		s.logger.Error("Failed to update job status", zap.String("id", job.ID), zap.Error(err))
		return
	}

	startTime := time.Now()
	s.logger.Info("Starting job execution", zap.String("id", job.ID), zap.String("type", job.Type))

	// Execute the job with timeout
	jobCtx, cancel := context.WithTimeout(ctx, s.config.JobTimeout)
	defer cancel()

	err := handler.Handle(jobCtx, job)
	duration := time.Since(startTime)

	if err != nil {
		s.logger.Error("Job execution failed",
			zap.String("id", job.ID),
			zap.Error(err),
			zap.Duration("duration", duration))

		job.Status = scheduler.JobStatusFailed
		job.Error = err.Error()
		job.Retries++

		// Retry logic
		if job.Retries < job.MaxRetries {
			job.Status = scheduler.JobStatusPending
			job.Schedule = time.Now().Add(s.config.RetryDelay)
			s.logger.Info("Job will be retried",
				zap.String("id", job.ID),
				zap.Int("retry", job.Retries),
				zap.Time("next_run", job.Schedule))
		}
	} else {
		s.logger.Info("Job executed successfully",
			zap.String("id", job.ID),
			zap.Duration("duration", duration))

		job.Status = scheduler.JobStatusCompleted
		completedAt := time.Now()
		job.CompletedAt = &completedAt
	}

	job.UpdatedAt = time.Now()
	if err := s.repository.Update(ctx, job); err != nil {
		s.logger.Error("Failed to update job after execution", zap.String("id", job.ID), zap.Error(err))
	}
}
