package port

import (
	"context"

	entity "github.com/quantuanhuy/lib/src/core/entity/task"
)

type ITaskRepoPort interface {
	Save(ctx context.Context, task *entity.Task) error
	GetByID(ctx context.Context, ID string) (*entity.Task, error)
	GetByStatus(ctx context.Context, status entity.TaskStatus) ([]*entity.Task, error)
	Update(ctx context.Context, task *entity.Task) error
	Delete(ctx context.Context, ID string) error
	GetTaskByType(ctx context.Context, taskType string) ([]*entity.Task, error)
}
