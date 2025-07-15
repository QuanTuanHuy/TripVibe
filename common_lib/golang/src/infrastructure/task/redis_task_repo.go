package task

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	entity "github.com/quantuanhuy/lib/src/core/entity/task"
	"github.com/quantuanhuy/lib/src/core/port"
	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"
)

const (
	TaskKeyPrefix      = "task:%s"
	TaskTypePrefix     = "task:type:%s"
	TaskStatusPrefix   = "task:status:%s"
	TaskPriorityPrefix = "task:priority:%d"
)

type RedisTaskRepo struct {
	client *redis.Client
	logger *zap.Logger
}

func (r *RedisTaskRepo) Save(ctx context.Context, task *entity.Task) error {
	key := fmt.Sprintf(TaskKeyPrefix, task.ID)

	data, err := json.Marshal(task)
	if err != nil {
		r.logger.Error("failed to marshal task",
			zap.Error(err))
		return err
	}

	if err := r.client.Set(ctx, key, data, 0).Err(); err != nil {
		r.logger.Error("failed to save task to Redis",
			zap.Error(err), zap.String("task_id", task.ID))
		return err
	}

	typeKey := fmt.Sprintf(TaskTypePrefix, task.Type)
	if err := r.client.SAdd(ctx, typeKey, task.ID).Err(); err != nil {
		r.logger.Error("failed to add task ID to type set in Redis",
			zap.Error(err), zap.String("task_id", task.ID), zap.String("task_type", task.Type))
	}

	r.logger.Debug("Task saved",
		zap.String("task_id", task.ID),
		zap.String("type", task.Type),
		zap.String("status", string(task.Status)),
		zap.Int("priority", int(task.Priority)))

	return nil
}

func (r *RedisTaskRepo) GetByID(ctx context.Context, ID string) (*entity.Task, error) {
	key := fmt.Sprintf(TaskKeyPrefix, ID)
	data, err := r.client.Get(ctx, key).Result()
	if err != nil {
		if err == redis.Nil {
			r.logger.Error("task not found in Redis",
				zap.String("task_id", ID))
			return nil, fmt.Errorf("task not found: %s", ID)
		}
		return nil, fmt.Errorf("failed to get task from Redis: %w", err)
	}

	var task entity.Task
	if err := json.Unmarshal([]byte(data), &task); err != nil {
		r.logger.Error("failed to unmarshal task data",
			zap.Error(err), zap.String("task_id", ID))
		return nil, fmt.Errorf("failed to unmarshal task: %w", err)
	}

	return &task, nil
}

func (r *RedisTaskRepo) Update(ctx context.Context, task *entity.Task) error {
	oldTask, err := r.GetByID(ctx, task.ID)
	if err != nil {
		r.logger.Error("failed to get task for update",
			zap.Error(err), zap.String("task_id", task.ID))
		return err
	}

	key := fmt.Sprintf(TaskKeyPrefix, task.ID)
	task.UpdatedAt = time.Now()

	data, err := json.Marshal(task)
	if err != nil {
		r.logger.Error("failed to marshal updated task",
			zap.Error(err), zap.String("task_id", task.ID))
		return fmt.Errorf("failed to marshal updated task: %w", err)
	}

	err = r.client.Set(ctx, key, data, 0).Err()
	if err != nil {
		r.logger.Error("failed to update task in Redis",
			zap.Error(err), zap.String("task_id", task.ID))
		return fmt.Errorf("failed to update task in Redis: %w", err)
	}

	if oldTask.Status != task.Status {
		oldStatusKey := fmt.Sprintf(TaskStatusPrefix, oldTask.Status)
		r.client.SRem(ctx, oldStatusKey, task.ID)

		newStatusKey := fmt.Sprintf(TaskStatusPrefix, task.Status)
		r.client.SAdd(ctx, newStatusKey, task.ID)
	}
	if oldTask.Priority != task.Priority {
		oldPriorityKey := fmt.Sprintf(TaskPriorityPrefix, int(oldTask.Priority))
		r.client.SRem(ctx, oldPriorityKey, task.ID)

		newPriorityKey := fmt.Sprintf(TaskPriorityPrefix, int(task.Priority))
		r.client.SAdd(ctx, newPriorityKey, task.ID)
	}
	if oldTask.Type != task.Type {
		oldTypeKey := fmt.Sprintf(TaskTypePrefix, oldTask.Type)
		r.client.SRem(ctx, oldTypeKey, task.ID)

		newTypeKey := fmt.Sprintf(TaskTypePrefix, task.Type)
		r.client.SAdd(ctx, newTypeKey, task.ID)
	}

	r.logger.Debug("Task updated",
		zap.String("task_id", task.ID),
		zap.String("type", task.Type))

	return nil
}

func (r *RedisTaskRepo) Delete(ctx context.Context, ID string) error {
	task, err := r.GetByID(ctx, ID)
	if err != nil {
		return fmt.Errorf("failed to get task for deletion: %w", err)
	}

	typeKey := fmt.Sprintf(TaskTypePrefix, task.Type)
	r.client.SRem(ctx, typeKey, ID)

	statusKey := fmt.Sprintf(TaskStatusPrefix, task.Status)
	r.client.SRem(ctx, statusKey, ID)

	priorityKey := fmt.Sprintf(TaskPriorityPrefix, int(task.Priority))
	r.client.SRem(ctx, priorityKey, ID)

	key := fmt.Sprintf(TaskKeyPrefix, ID)
	if err := r.client.Del(ctx, key).Err(); err != nil {
		r.logger.Error("failed to delete task from Redis",
			zap.Error(err), zap.String("task_id", ID))
		return fmt.Errorf("failed to delete task: %w", err)
	}

	r.logger.Debug("Task deleted",
		zap.String("task_id", ID))

	return nil
}

func (r *RedisTaskRepo) GetByStatus(ctx context.Context, status entity.TaskStatus) ([]*entity.Task, error) {
	statusKey := fmt.Sprintf(TaskStatusPrefix, status)

	taskIDs, err := r.client.SMembers(ctx, statusKey).Result()
	if err != nil {
		r.logger.Error("failed to get task IDs by status from Redis",
			zap.Error(err), zap.String("status", string(status)))
		return nil, fmt.Errorf("failed to get task IDs by status: %w", err)
	}

	tasks := make([]*entity.Task, 0, len(taskIDs))
	for _, id := range taskIDs {
		task, err := r.GetByID(ctx, id)
		if err != nil {
			r.logger.Error("failed to get task by ID",
				zap.Error(err), zap.String("task_id", id))
			continue
		}
		tasks = append(tasks, task)
	}

	return tasks, nil
}

func (r *RedisTaskRepo) GetTaskByType(ctx context.Context, taskType string) ([]*entity.Task, error) {
	typeKey := fmt.Sprintf(TaskTypePrefix, taskType)

	taskIDs, err := r.client.SMembers(ctx, typeKey).Result()
	if err != nil {
		r.logger.Error("failed to get task IDs by type from Redis",
			zap.Error(err), zap.String("task_type", taskType))
		return nil, fmt.Errorf("failed to get task IDs by type: %w", err)
	}

	tasks := make([]*entity.Task, 0, len(taskIDs))
	for _, id := range taskIDs {
		task, err := r.GetByID(ctx, id)
		if err != nil {
			r.logger.Error("failed to get task by ID",
				zap.Error(err), zap.String("task_id", id))
			continue
		}
		tasks = append(tasks, task)
	}

	return tasks, nil
}

func NewRedisTaskRepo(client *redis.Client, logger *zap.Logger) port.ITaskRepoPort {
	return &RedisTaskRepo{
		client: client,
		logger: logger,
	}
}
