package port

import (
	"context"

	entity "github.com/quantuanhuy/lib/src/core/entity/task"
)

// TaskHandler defines the interface for task handlers
type TaskHandler interface {
	Handle(ctx context.Context, task *entity.Task) error
	GetType() string
}

type ITaskWorkerPort interface {
	Start(ctx context.Context) error
	Stop(ctx context.Context) error
	RegisterHandler(handler TaskHandler) error
	GetStats(ctx context.Context) (*entity.WorkerStats, error)
}
