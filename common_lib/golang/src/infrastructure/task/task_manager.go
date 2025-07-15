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

type TaskManager struct {
	queue      port.TaskQueue
	repository port.TaskRepository
	worker     port.TaskWorker
	logger     *zap.Logger
	mu         sync.RWMutex
	running    bool
}

func NewTaskManager(queue port.TaskQueue, repository port.TaskRepository, worker port.TaskWorker, logger *zap.Logger) port.TaskManager {
	return &TaskManager{
		queue:      queue,
		repository: repository,
		worker:     worker,
		logger:     logger,
	}
}

func (m *TaskManager) SubmitTask(ctx context.Context, t *task.Task) error {
	t.Status = task.TaskStatusPending
	t.CreatedAt = time.Now()
	t.UpdatedAt = time.Now()

	// Save to repository
	if err := m.repository.Save(ctx, t); err != nil {
		return fmt.Errorf("failed to save task: %w", err)
	}

	// Add to queue
	if err := m.queue.Enqueue(ctx, t); err != nil {
		return fmt.Errorf("failed to enqueue task: %w", err)
	}

	m.logger.Info("Task submitted",
		zap.String("task_id", t.ID),
		zap.String("type", t.Type),
		zap.Int("priority", int(t.Priority)))

	return nil
}

func (m *TaskManager) SubmitDelayedTask(ctx context.Context, t *task.Task, delay time.Duration) error {
	t.Status = task.TaskStatusPending
	t.Delay = delay
	t.CreatedAt = time.Now()
	t.UpdatedAt = time.Now()

	// Save to repository
	if err := m.repository.Save(ctx, t); err != nil {
		return fmt.Errorf("failed to save delayed task: %w", err)
	}

	// Schedule delayed execution
	go func() {
		time.Sleep(delay)
		if err := m.queue.Enqueue(ctx, t); err != nil {
			m.logger.Error("Failed to enqueue delayed task",
				zap.String("task_id", t.ID),
				zap.Error(err))
		}
	}()

	m.logger.Info("Delayed task scheduled",
		zap.String("task_id", t.ID),
		zap.String("type", t.Type),
		zap.Duration("delay", delay))

	return nil
}

func (m *TaskManager) CancelTask(ctx context.Context, taskID string) error {
	t, err := m.repository.GetByID(ctx, taskID)
	if err != nil {
		return fmt.Errorf("failed to get task: %w", err)
	}

	if t.Status == task.TaskStatusRunning {
		return fmt.Errorf("cannot cancel running task")
	}

	t.Status = task.TaskStatusCancelled
	t.UpdatedAt = time.Now()

	if err := m.repository.Update(ctx, t); err != nil {
		return fmt.Errorf("failed to update task status: %w", err)
	}

	m.logger.Info("Task cancelled", zap.String("task_id", taskID))
	return nil
}

func (m *TaskManager) GetTaskStatus(ctx context.Context, taskID string) (*task.Task, error) {
	return m.repository.GetByID(ctx, taskID)
}

func (m *TaskManager) GetQueueStatus(ctx context.Context) (*task.TaskQueue, error) {
	size, err := m.queue.Size(ctx)
	if err != nil {
		return nil, fmt.Errorf("failed to get queue size: %w", err)
	}

	return &task.TaskQueue{
		Name:        "default",
		MaxSize:     1000, // This should be configurable
		CurrentSize: size,
		CreatedAt:   time.Now(),
	}, nil
}

func (m *TaskManager) Start(ctx context.Context) error {
	m.mu.Lock()
	defer m.mu.Unlock()

	if m.running {
		return fmt.Errorf("task manager is already running")
	}

	m.running = true

	// Start the worker
	if err := m.worker.Start(ctx); err != nil {
		return fmt.Errorf("failed to start worker: %w", err)
	}

	m.logger.Info("Task manager started")
	return nil
}

func (m *TaskManager) Stop(ctx context.Context) error {
	m.mu.Lock()
	defer m.mu.Unlock()

	if !m.running {
		return fmt.Errorf("task manager is not running")
	}

	m.running = false

	// Stop the worker
	if err := m.worker.Stop(ctx); err != nil {
		return fmt.Errorf("failed to stop worker: %w", err)
	}

	m.logger.Info("Task manager stopped")
	return nil
}
