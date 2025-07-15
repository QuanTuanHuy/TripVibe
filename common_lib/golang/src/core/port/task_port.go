package port

import (
	"context"
	"time"

	"github.com/quantuanhuy/lib/src/core/entity/task"
)

// TaskRepository defines the interface for task persistence
type TaskRepository interface {
	Save(ctx context.Context, task *task.Task) error
	GetByID(ctx context.Context, id string) (*task.Task, error)
	GetByStatus(ctx context.Context, status task.TaskStatus) ([]*task.Task, error)
	GetPendingTasks(ctx context.Context, limit int) ([]*task.Task, error)
	GetTasksByPriority(ctx context.Context, priority task.TaskPriority, limit int) ([]*task.Task, error)
	Update(ctx context.Context, task *task.Task) error
	Delete(ctx context.Context, id string) error
	GetTasksByType(ctx context.Context, taskType string) ([]*task.Task, error)
	CleanupCompletedTasks(ctx context.Context, olderThan time.Time) error
}

// TaskQueue defines the interface for task queue operations
type TaskQueue interface {
	Enqueue(ctx context.Context, task *task.Task) error
	Dequeue(ctx context.Context) (*task.Task, error)
	DequeueWithPriority(ctx context.Context) (*task.Task, error)
	Size(ctx context.Context) (int, error)
	IsEmpty(ctx context.Context) (bool, error)
	Clear(ctx context.Context) error
}

// TaskWorker defines the interface for task workers
type TaskWorker interface {
	Start(ctx context.Context) error
	Stop(ctx context.Context) error
	RegisterHandler(handler task.TaskHandler) error
	GetStats(ctx context.Context) (*WorkerStats, error)
}

// WorkerStats represents worker statistics
type WorkerStats struct {
	TotalProcessed int           `json:"total_processed"`
	TotalFailed    int           `json:"total_failed"`
	ActiveTasks    int           `json:"active_tasks"`
	Uptime         time.Duration `json:"uptime"`
	LastActivity   time.Time     `json:"last_activity"`
}

// TaskManager defines the interface for task management
type TaskManager interface {
	SubmitTask(ctx context.Context, task *task.Task) error
	SubmitDelayedTask(ctx context.Context, task *task.Task, delay time.Duration) error
	CancelTask(ctx context.Context, taskID string) error
	GetTaskStatus(ctx context.Context, taskID string) (*task.Task, error)
	GetQueueStatus(ctx context.Context) (*task.TaskQueue, error)
	Start(ctx context.Context) error
	Stop(ctx context.Context) error
}
