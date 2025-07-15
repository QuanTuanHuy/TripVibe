package batch

import (
	"context"
	"fmt"
	"sync"
	"time"

	"github.com/quantuanhuy/lib/src/core/entity/scheduler"
	"github.com/quantuanhuy/lib/src/core/port"
	"go.uber.org/zap"
)

type BatchProcessor struct {
	repository port.BatchJobRepository
	logger     *zap.Logger
	processors map[string]scheduler.BatchProcessor
	mu         sync.RWMutex
	running    bool
	stopCh     chan struct{}
}

func NewBatchProcessor(repository port.BatchJobRepository, logger *zap.Logger) port.BatchProcessor {
	return &BatchProcessor{
		repository: repository,
		logger:     logger,
		processors: make(map[string]scheduler.BatchProcessor),
		stopCh:     make(chan struct{}),
	}
}

func (bp *BatchProcessor) RegisterProcessor(processor scheduler.BatchProcessor) error {
	bp.mu.Lock()
	defer bp.mu.Unlock()

	bp.processors[processor.GetType()] = processor
	bp.logger.Info("Registered batch processor", zap.String("type", processor.GetType()))
	return nil
}

func (bp *BatchProcessor) ProcessBatch(ctx context.Context, job *scheduler.BatchJob, processor scheduler.BatchProcessor) error {
	bp.logger.Info("Starting batch processing",
		zap.String("job_id", job.ID),
		zap.String("type", job.Type),
		zap.Int("total_items", job.TotalItems))

	job.Status = scheduler.JobStatusRunning
	job.UpdatedAt = time.Now()

	if err := bp.repository.Update(ctx, job); err != nil {
		return fmt.Errorf("failed to update job status: %w", err)
	}

	// Process in batches
	batchSize := job.BatchSize
	if batchSize <= 0 {
		batchSize = 100 // Default batch size
	}

	// Mock data - in real implementation, this would come from the job payload
	var allItems []interface{}
	for i := 0; i < job.TotalItems; i++ {
		allItems = append(allItems, fmt.Sprintf("item_%d", i))
	}

	processedItems := 0
	for i := 0; i < len(allItems); i += batchSize {
		end := i + batchSize
		if end > len(allItems) {
			end = len(allItems)
		}

		batch := allItems[i:end]

		bp.logger.Debug("Processing batch",
			zap.String("job_id", job.ID),
			zap.Int("batch_start", i),
			zap.Int("batch_end", end))

		if err := processor.ProcessBatch(ctx, job, batch); err != nil {
			bp.logger.Error("Batch processing failed",
				zap.String("job_id", job.ID),
				zap.Int("batch_start", i),
				zap.Error(err))

			job.Status = scheduler.JobStatusFailed
			job.Error = err.Error()
			job.UpdatedAt = time.Now()

			if err := bp.repository.Update(ctx, job); err != nil {
				bp.logger.Error("Failed to update job after error",
					zap.String("job_id", job.ID),
					zap.Error(err))
			}

			return fmt.Errorf("batch processing failed: %w", err)
		}

		processedItems += len(batch)
		job.ProcessedItems = processedItems
		job.UpdatedAt = time.Now()

		if err := bp.repository.Update(ctx, job); err != nil {
			bp.logger.Error("Failed to update job progress",
				zap.String("job_id", job.ID),
				zap.Error(err))
		}
	}

	// Mark job as completed
	job.Status = scheduler.JobStatusCompleted
	job.ProcessedItems = job.TotalItems
	job.UpdatedAt = time.Now()
	completedAt := time.Now()
	job.CompletedAt = &completedAt

	if err := bp.repository.Update(ctx, job); err != nil {
		return fmt.Errorf("failed to update job completion: %w", err)
	}

	bp.logger.Info("Batch processing completed",
		zap.String("job_id", job.ID),
		zap.Int("processed_items", processedItems))

	return nil
}

func (bp *BatchProcessor) Start(ctx context.Context) error {
	bp.mu.Lock()
	defer bp.mu.Unlock()

	if bp.running {
		return fmt.Errorf("batch processor is already running")
	}

	bp.running = true

	// Start processing loop
	go bp.processLoop(ctx)

	bp.logger.Info("Batch processor started")
	return nil
}

func (bp *BatchProcessor) Stop(ctx context.Context) error {
	bp.mu.Lock()
	defer bp.mu.Unlock()

	if !bp.running {
		return fmt.Errorf("batch processor is not running")
	}

	bp.running = false
	close(bp.stopCh)

	bp.logger.Info("Batch processor stopped")
	return nil
}

func (bp *BatchProcessor) processLoop(ctx context.Context) {
	ticker := time.NewTicker(time.Second * 1) // Check every 1 second for tests
	defer ticker.Stop()

	for {
		select {
		case <-ctx.Done():
			return
		case <-bp.stopCh:
			return
		case <-ticker.C:
			bp.processPendingJobs(ctx)
		}
	}
}

func (bp *BatchProcessor) processPendingJobs(ctx context.Context) {
	jobs, err := bp.repository.GetByStatus(ctx, scheduler.JobStatusPending)
	if err != nil {
		bp.logger.Error("Failed to get pending batch jobs", zap.Error(err))
		return
	}

	for _, job := range jobs {
		bp.mu.RLock()
		processor, exists := bp.processors[job.Type]
		bp.mu.RUnlock()

		if !exists {
			bp.logger.Error("No processor found for job type",
				zap.String("job_id", job.ID),
				zap.String("type", job.Type))
			continue
		}

		// Process job in background
		go func(j *scheduler.BatchJob) {
			if err := bp.ProcessBatch(ctx, j, processor); err != nil {
				bp.logger.Error("Failed to process batch job",
					zap.String("job_id", j.ID),
					zap.Error(err))
			}
		}(job)
	}
}
