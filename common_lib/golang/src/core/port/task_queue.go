package port

import (
	"context"

	entity "github.com/quantuanhuy/lib/src/core/entity/task"
)

type ITaskQueuePort interface {
	Enqueue(ctx context.Context, task *entity.Task) error
	Dequeue(ctx context.Context) (*entity.Task, error)
	DequeueWithPriority(ctx context.Context) (*entity.Task, error)
	Size(ctx context.Context) (int, error)
	IsEmpty(ctx context.Context) (bool, error)
	Clear(ctx context.Context) error
}
