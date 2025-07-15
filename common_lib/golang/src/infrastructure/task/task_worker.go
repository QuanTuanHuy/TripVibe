package task

import (
	"context"
	"fmt"
	"sync"
	"time"

	"github.com/quantuanhuy/lib/src/core/entity/task"
	"github.com/quantuanhuy/lib/src/core/port"
	"go.uber.org/zap"
)

type TaskWorker struct {
	id         string
	queue      port.TaskQueue
	repository port.TaskRepository
	logger     *zap.Logger
	config     *task.WorkerConfig
	handlers   map[string]task.TaskHandler
	mu         sync.RWMutex
	running    bool
	stopCh     chan struct{}
	stats      *port.WorkerStats
	startTime  time.Time
}

func NewTaskWorker(id string, queue port.TaskQueue, repository port.TaskRepository, logger *zap.Logger, config *task.WorkerConfig) port.TaskWorker {
	return &TaskWorker{
		id:         id,
		queue:      queue,
		repository: repository,
		logger:     logger,
		config:     config,
		handlers:   make(map[string]task.TaskHandler),
		stopCh:     make(chan struct{}),
		stats: &port.WorkerStats{
			TotalProcessed: 0,
			TotalFailed:    0,
			ActiveTasks:    0,
			Uptime:         0,
			LastActivity:   time.Now(),
		},
	}
}

func (w *TaskWorker) RegisterHandler(handler task.TaskHandler) error {
	w.mu.Lock()
	defer w.mu.Unlock()

	w.handlers[handler.GetType()] = handler
	w.logger.Info("Registered task handler",
		zap.String("worker_id", w.id),
		zap.String("type", handler.GetType()))
	return nil
}

func (w *TaskWorker) Start(ctx context.Context) error {
	w.mu.Lock()
	defer w.mu.Unlock()

	if w.running {
		return fmt.Errorf("worker %s is already running", w.id)
	}

	w.running = true
	w.startTime = time.Now()

	// Start worker goroutines
	for i := 0; i < w.config.MaxConcurrentTasks; i++ {
		go w.processTask(ctx, i)
	}

	w.logger.Info("Task worker started",
		zap.String("worker_id", w.id),
		zap.Int("concurrent_tasks", w.config.MaxConcurrentTasks))

	return nil
}

func (w *TaskWorker) Stop(ctx context.Context) error {
	w.mu.Lock()
	defer w.mu.Unlock()

	if !w.running {
		return fmt.Errorf("worker %s is not running", w.id)
	}

	w.running = false
	close(w.stopCh)

	w.logger.Info("Task worker stopped", zap.String("worker_id", w.id))
	return nil
}

func (w *TaskWorker) GetStats(ctx context.Context) (*port.WorkerStats, error) {
	w.mu.RLock()
	defer w.mu.RUnlock()

	stats := *w.stats
	if w.running {
		stats.Uptime = time.Since(w.startTime)
	}

	return &stats, nil
}

func (w *TaskWorker) processTask(ctx context.Context, workerID int) {
	ticker := time.NewTicker(w.config.PollInterval)
	defer ticker.Stop()

	for {
		select {
		case <-ctx.Done():
			return
		case <-w.stopCh:
			return
		case <-ticker.C:
			w.processNextTask(ctx, workerID)
		}
	}
}

func (w *TaskWorker) processNextTask(ctx context.Context, workerID int) {
	// Get next task from queue
	task, err := w.queue.DequeueWithPriority(ctx)
	if err != nil {
		w.logger.Error("Failed to dequeue task",
			zap.String("worker_id", w.id),
			zap.Int("worker_num", workerID),
			zap.Error(err))
		return
	}

	if task == nil {
		return // No task available
	}

	// Update stats
	w.mu.Lock()
	w.stats.ActiveTasks++
	w.stats.LastActivity = time.Now()
	w.mu.Unlock()

	// Process the task
	w.executeTask(ctx, task)

	// Update stats
	w.mu.Lock()
	w.stats.ActiveTasks--
	w.stats.TotalProcessed++
	w.mu.Unlock()
}

func (w *TaskWorker) executeTask(ctx context.Context, t *task.Task) {
	w.mu.RLock()
	handler, exists := w.handlers[t.Type]
	w.mu.RUnlock()

	if !exists {
		w.logger.Error("No handler found for task type",
			zap.String("worker_id", w.id),
			zap.String("task_id", t.ID),
			zap.String("type", t.Type))

		t.Status = task.TaskStatusFailed
		t.Error = fmt.Sprintf("No handler found for task type: %s", t.Type)
		t.UpdatedAt = time.Now()
		w.repository.Update(ctx, t)
		return
	}

	// Update task status to running
	t.Status = task.TaskStatusRunning
	startedAt := time.Now()
	t.StartedAt = &startedAt
	t.UpdatedAt = time.Now()

	if err := w.repository.Update(ctx, t); err != nil {
		w.logger.Error("Failed to update task status",
			zap.String("worker_id", w.id),
			zap.String("task_id", t.ID),
			zap.Error(err))
		return
	}

	w.logger.Info("Starting task execution",
		zap.String("worker_id", w.id),
		zap.String("task_id", t.ID),
		zap.String("type", t.Type))

	// Execute the task with timeout
	taskCtx, cancel := context.WithTimeout(ctx, w.config.TaskTimeout)
	defer cancel()

	startTime := time.Now()
	err := handler.Handle(taskCtx, t)
	duration := time.Since(startTime)

	if err != nil {
		w.logger.Error("Task execution failed",
			zap.String("worker_id", w.id),
			zap.String("task_id", t.ID),
			zap.Error(err),
			zap.Duration("duration", duration))

		t.Status = task.TaskStatusFailed
		t.Error = err.Error()
		t.Retries++

		w.mu.Lock()
		w.stats.TotalFailed++
		w.mu.Unlock()

		// Retry logic
		if t.Retries < t.MaxRetries {
			t.Status = task.TaskStatusPending
			t.Delay = w.config.RetryDelay

			w.logger.Info("Task will be retried",
				zap.String("worker_id", w.id),
				zap.String("task_id", t.ID),
				zap.Int("retry", t.Retries))

			// Re-enqueue the task
			if err := w.queue.Enqueue(ctx, t); err != nil {
				w.logger.Error("Failed to re-enqueue task",
					zap.String("worker_id", w.id),
					zap.String("task_id", t.ID),
					zap.Error(err))
			}
		}
	} else {
		w.logger.Info("Task executed successfully",
			zap.String("worker_id", w.id),
			zap.String("task_id", t.ID),
			zap.Duration("duration", duration))

		t.Status = task.TaskStatusCompleted
		completedAt := time.Now()
		t.CompletedAt = &completedAt
	}

	t.UpdatedAt = time.Now()
	if err := w.repository.Update(ctx, t); err != nil {
		w.logger.Error("Failed to update task after execution",
			zap.String("worker_id", w.id),
			zap.String("task_id", t.ID),
			zap.Error(err))
	}
}
