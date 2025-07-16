package worker

import (
	"context"
	"fmt"
	"sync"
	"time"

	"github.com/quantuanhuy/lib/src/config"
	entity "github.com/quantuanhuy/lib/src/core/entity/task"
	"github.com/quantuanhuy/lib/src/core/port"
	"go.uber.org/zap"
)

type TaskWorker struct {
	id         string
	queue      port.ITaskQueuePort
	repository port.ITaskRepoPort
	logger     *zap.Logger
	config     *config.WorkerConfig
	handlers   map[string]port.TaskHandler
	mu         sync.RWMutex
	running    bool
	stopCh     chan struct{}
	stats      entity.WorkerStats
	startTime  time.Time
}

func (t *TaskWorker) RegisterHandler(handler port.TaskHandler) error {
	t.mu.Lock()
	defer t.mu.Unlock()

	t.handlers[handler.GetType()] = handler
	t.logger.Info("Task handler registered",
		zap.String("task_type", handler.GetType()),
		zap.String("worker_id", t.id))
	return nil
}

func (t *TaskWorker) Start(ctx context.Context) error {
	t.mu.Lock()
	defer t.mu.Unlock()

	if t.running {
		return fmt.Errorf("worker %s is already running", t.id)
	}

	t.running = true
	t.startTime = time.Now()

	// start worker goroutines
	for i := range t.config.MaxConcurrentTasks {
		go t.processTasks(ctx, i)
	}

	t.logger.Info("Task worker started",
		zap.String("worker_id", t.id),
		zap.Int("max_concurrent_tasks", t.config.MaxConcurrentTasks),
		zap.Time("start_time", t.startTime))
	return nil
}

func (t *TaskWorker) processTasks(ctx context.Context, workerID int) {
	ticker := time.NewTicker(t.config.PollInterval)
	defer ticker.Stop()

	for {
		select {
		case <-t.stopCh:
			return
		case <-ctx.Done():
			return
		case <-ticker.C:
			t.processNextTask(ctx, workerID)
		}
	}
}

func (t *TaskWorker) processNextTask(ctx context.Context, workerID int) {
	task, err := t.queue.DequeueWithPriority(ctx)
	if err != nil {
		t.logger.Error("Failed to dequeue task",
			zap.String("worker_id", t.id),
			zap.Error(err))
		return
	}
	if task == nil {
		return
	}

	t.mu.Lock()
	t.stats.ActiveTasks++
	t.stats.LastActivity = time.Now()
	t.mu.Unlock()

	t.executeTask(ctx, task, workerID)

	t.mu.Lock()
	t.stats.ActiveTasks--
	t.stats.TotalProcessed++
	if task.Status == entity.TaskStatusFailed {
		t.stats.TotalFailed++
	}
	t.mu.Unlock()
}

func (t *TaskWorker) executeTask(ctx context.Context, task *entity.Task, workerID int) {
	t.mu.RLock()
	handler, exists := t.handlers[task.Type]
	t.mu.RUnlock()
	if !exists {
		t.logger.Error("No handler registered for task type",
			zap.String("task_type", task.Type),
			zap.String("worker_id", t.id))

		task.Status = entity.TaskStatusFailed
		task.Error = fmt.Sprintf("No handler registered for task type: %s", task.Type)
		task.UpdatedAt = time.Now()
		t.repository.Update(ctx, task)
		return
	}

	task.Status = entity.TaskStatusRunning
	startedAt := time.Now()
	task.StartedAt = &startedAt
	task.UpdatedAt = startedAt

	if err := t.repository.Update(ctx, task); err != nil {
		t.logger.Error("Failed to update task status to running",
			zap.String("task_id", task.ID),
			zap.Error(err))
		return
	}

	taskCtx, cancel := context.WithTimeout(ctx, t.config.TaskTimeout)
	defer cancel()

	err := handler.Handle(taskCtx, task)
	if err != nil {
		t.logger.Error("Task execution failed",
			zap.String("task_id", task.ID),
			zap.String("task_type", task.Type),
			zap.Error(err))

		task.Status = entity.TaskStatusFailed
		task.Error = err.Error()
		task.Retries++

		// Retry logic
		if task.Retries < task.MaxRetries {
			task.Status = entity.TaskStatusPending
			task.Delay = t.config.RetryDelay

			t.logger.Info("Retrying task",
				zap.String("task_id", task.ID),
				zap.Int("retries", task.Retries),
				zap.Int("worker_id", workerID))

			// Handle delay before re-enqueueing
			if task.Delay > 0 {
				// Start a goroutine to handle delayed re-enqueueing
				go func(delayedTask *entity.Task) {
					time.Sleep(delayedTask.Delay)
					if err := t.queue.Enqueue(ctx, delayedTask); err != nil {
						t.logger.Error("Failed to re-enqueue delayed task",
							zap.String("task_id", delayedTask.ID),
							zap.Error(err))
					}
				}(task)
			} else {
				// Re-enqueue immediately
				if err := t.queue.Enqueue(ctx, task); err != nil {
					t.logger.Error("Failed to re-enqueue task",
						zap.String("task_id", task.ID),
						zap.Error(err))
				}
			}
		}
	} else {
		t.logger.Info("Task executed successfully",
			zap.String("task_id", task.ID),
			zap.String("task_type", task.Type))

		task.Status = entity.TaskStatusCompleted
		completedAt := time.Now()
		task.CompletedAt = &completedAt
	}

	task.UpdatedAt = time.Now()
	if err := t.repository.Update(ctx, task); err != nil {
		t.logger.Error("Failed to update task status after execution",
			zap.String("task_id", task.ID),
			zap.Error(err))
		return
	}
}

func (t *TaskWorker) Stop(ctx context.Context) error {
	t.mu.Lock()
	defer t.mu.Unlock()

	if !t.running {
		return fmt.Errorf("worker %s is not running", t.id)
	}

	t.running = false
	close(t.stopCh)

	t.logger.Info("Task worker stopped",
		zap.String("worker_id", t.id),
		zap.Time("stop_time", time.Now()))

	return nil
}

func (t *TaskWorker) GetStats(ctx context.Context) (*entity.WorkerStats, error) {
	t.mu.RLock()
	defer t.mu.RUnlock()

	stats := t.stats
	if t.running {
		stats.Uptime = time.Since(t.startTime)
	}
	return &stats, nil
}

func NewTaskWorker(
	id string,
	queue port.ITaskQueuePort,
	repository port.ITaskRepoPort,
	logger *zap.Logger,
	config *config.WorkerConfig,
) port.ITaskWorkerPort {
	return &TaskWorker{
		id:         id,
		queue:      queue,
		repository: repository,
		logger:     logger,
		config:     config,
		handlers:   make(map[string]port.TaskHandler),
		stopCh:     make(chan struct{}),
		stats: entity.WorkerStats{
			TotalProcessed: 0,
			TotalFailed:    0,
			ActiveTasks:    0,
			Uptime:         0,
			LastActivity:   time.Now(),
		},
	}
}
