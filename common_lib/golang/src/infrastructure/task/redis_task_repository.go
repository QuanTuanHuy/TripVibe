package task

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/quantuanhuy/lib/src/core/entity/task"
	"github.com/quantuanhuy/lib/src/core/port"
	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"
)

type RedisTaskRepository struct {
	client *redis.Client
	logger *zap.Logger
}

func NewRedisTaskRepository(client *redis.Client, logger *zap.Logger) port.TaskRepository {
	return &RedisTaskRepository{
		client: client,
		logger: logger,
	}
}

func (r *RedisTaskRepository) Save(ctx context.Context, t *task.Task) error {
	key := fmt.Sprintf("task:%s", t.ID)

	data, err := json.Marshal(t)
	if err != nil {
		return fmt.Errorf("failed to marshal task: %w", err)
	}

	err = r.client.Set(ctx, key, data, 0).Err()
	if err != nil {
		return fmt.Errorf("failed to save task: %w", err)
	}

	// Add to type index
	typeKey := fmt.Sprintf("task:type:%s", t.Type)
	err = r.client.SAdd(ctx, typeKey, t.ID).Err()
	if err != nil {
		r.logger.Error("Failed to add task to type index", zap.Error(err))
	}

	// Add to status index
	statusKey := fmt.Sprintf("task:status:%s", t.Status)
	err = r.client.SAdd(ctx, statusKey, t.ID).Err()
	if err != nil {
		r.logger.Error("Failed to add task to status index", zap.Error(err))
	}

	// Add to priority index
	priorityKey := fmt.Sprintf("task:priority:%d", t.Priority)
	err = r.client.SAdd(ctx, priorityKey, t.ID).Err()
	if err != nil {
		r.logger.Error("Failed to add task to priority index", zap.Error(err))
	}

	r.logger.Debug("Task saved",
		zap.String("task_id", t.ID),
		zap.String("type", t.Type),
		zap.String("status", string(t.Status)),
		zap.Int("priority", int(t.Priority)))

	return nil
}

func (r *RedisTaskRepository) GetByID(ctx context.Context, id string) (*task.Task, error) {
	key := fmt.Sprintf("task:%s", id)

	data, err := r.client.Get(ctx, key).Result()
	if err != nil {
		if err == redis.Nil {
			return nil, fmt.Errorf("task not found: %s", id)
		}
		return nil, fmt.Errorf("failed to get task: %w", err)
	}

	var t task.Task
	err = json.Unmarshal([]byte(data), &t)
	if err != nil {
		return nil, fmt.Errorf("failed to unmarshal task: %w", err)
	}

	return &t, nil
}

func (r *RedisTaskRepository) Update(ctx context.Context, t *task.Task) error {
	// Get old task to update indexes
	oldTask, err := r.GetByID(ctx, t.ID)
	if err != nil {
		return fmt.Errorf("failed to get existing task: %w", err)
	}

	// Update the task
	key := fmt.Sprintf("task:%s", t.ID)
	t.UpdatedAt = time.Now()

	data, err := json.Marshal(t)
	if err != nil {
		return fmt.Errorf("failed to marshal task: %w", err)
	}

	err = r.client.Set(ctx, key, data, 0).Err()
	if err != nil {
		return fmt.Errorf("failed to update task: %w", err)
	}

	// Update indexes if changed
	if oldTask.Status != t.Status {
		// Remove from old status index
		oldStatusKey := fmt.Sprintf("task:status:%s", oldTask.Status)
		r.client.SRem(ctx, oldStatusKey, t.ID)

		// Add to new status index
		newStatusKey := fmt.Sprintf("task:status:%s", t.Status)
		r.client.SAdd(ctx, newStatusKey, t.ID)
	}

	if oldTask.Priority != t.Priority {
		// Remove from old priority index
		oldPriorityKey := fmt.Sprintf("task:priority:%d", oldTask.Priority)
		r.client.SRem(ctx, oldPriorityKey, t.ID)

		// Add to new priority index
		newPriorityKey := fmt.Sprintf("task:priority:%d", t.Priority)
		r.client.SAdd(ctx, newPriorityKey, t.ID)
	}

	if oldTask.Type != t.Type {
		// Remove from old type index
		oldTypeKey := fmt.Sprintf("task:type:%s", oldTask.Type)
		r.client.SRem(ctx, oldTypeKey, t.ID)

		// Add to new type index
		newTypeKey := fmt.Sprintf("task:type:%s", t.Type)
		r.client.SAdd(ctx, newTypeKey, t.ID)
	}

	r.logger.Debug("Task updated",
		zap.String("task_id", t.ID),
		zap.String("status", string(t.Status)))

	return nil
}

func (r *RedisTaskRepository) Delete(ctx context.Context, id string) error {
	// Get task to remove from indexes
	t, err := r.GetByID(ctx, id)
	if err != nil {
		return fmt.Errorf("failed to get task for deletion: %w", err)
	}

	// Remove from indexes
	typeKey := fmt.Sprintf("task:type:%s", t.Type)
	r.client.SRem(ctx, typeKey, id)

	statusKey := fmt.Sprintf("task:status:%s", t.Status)
	r.client.SRem(ctx, statusKey, id)

	priorityKey := fmt.Sprintf("task:priority:%d", t.Priority)
	r.client.SRem(ctx, priorityKey, id)

	// Delete the task
	key := fmt.Sprintf("task:%s", id)
	err = r.client.Del(ctx, key).Err()
	if err != nil {
		return fmt.Errorf("failed to delete task: %w", err)
	}

	r.logger.Debug("Task deleted", zap.String("task_id", id))
	return nil
}

func (r *RedisTaskRepository) GetByStatus(ctx context.Context, status task.TaskStatus) ([]*task.Task, error) {
	statusKey := fmt.Sprintf("task:status:%s", status)

	taskIDs, err := r.client.SMembers(ctx, statusKey).Result()
	if err != nil {
		return nil, fmt.Errorf("failed to get task IDs by status: %w", err)
	}

	tasks := make([]*task.Task, 0, len(taskIDs))
	for _, id := range taskIDs {
		t, err := r.GetByID(ctx, id)
		if err != nil {
			r.logger.Error("Failed to get task by ID", zap.String("task_id", id), zap.Error(err))
			continue
		}
		tasks = append(tasks, t)
	}

	return tasks, nil
}

func (r *RedisTaskRepository) GetTasksByType(ctx context.Context, taskType string) ([]*task.Task, error) {
	typeKey := fmt.Sprintf("task:type:%s", taskType)

	taskIDs, err := r.client.SMembers(ctx, typeKey).Result()
	if err != nil {
		return nil, fmt.Errorf("failed to get task IDs by type: %w", err)
	}

	tasks := make([]*task.Task, 0, len(taskIDs))
	for _, id := range taskIDs {
		t, err := r.GetByID(ctx, id)
		if err != nil {
			r.logger.Error("Failed to get task by ID", zap.String("task_id", id), zap.Error(err))
			continue
		}
		tasks = append(tasks, t)
	}

	return tasks, nil
}

func (r *RedisTaskRepository) GetTasksByPriority(ctx context.Context, priority task.TaskPriority, limit int) ([]*task.Task, error) {
	priorityKey := fmt.Sprintf("task:priority:%d", priority)

	var taskIDs []string
	var err error

	if limit > 0 {
		taskIDs, err = r.client.SRandMemberN(ctx, priorityKey, int64(limit)).Result()
	} else {
		taskIDs, err = r.client.SMembers(ctx, priorityKey).Result()
	}

	if err != nil {
		return nil, fmt.Errorf("failed to get task IDs by priority: %w", err)
	}

	tasks := make([]*task.Task, 0, len(taskIDs))
	for _, id := range taskIDs {
		t, err := r.GetByID(ctx, id)
		if err != nil {
			r.logger.Error("Failed to get task by ID", zap.String("task_id", id), zap.Error(err))
			continue
		}
		tasks = append(tasks, t)
	}

	return tasks, nil
}

func (r *RedisTaskRepository) GetPendingTasks(ctx context.Context, limit int) ([]*task.Task, error) {
	return r.GetTasksByStatus(ctx, task.TaskStatusPending, limit)
}

func (r *RedisTaskRepository) GetTasksByStatus(ctx context.Context, status task.TaskStatus, limit int) ([]*task.Task, error) {
	statusKey := fmt.Sprintf("task:status:%s", status)

	var taskIDs []string
	var err error

	if limit > 0 {
		taskIDs, err = r.client.SRandMemberN(ctx, statusKey, int64(limit)).Result()
	} else {
		taskIDs, err = r.client.SMembers(ctx, statusKey).Result()
	}

	if err != nil {
		return nil, fmt.Errorf("failed to get task IDs by status: %w", err)
	}

	tasks := make([]*task.Task, 0, len(taskIDs))
	for _, id := range taskIDs {
		t, err := r.GetByID(ctx, id)
		if err != nil {
			r.logger.Error("Failed to get task by ID", zap.String("task_id", id), zap.Error(err))
			continue
		}
		tasks = append(tasks, t)
	}

	return tasks, nil
}

func (r *RedisTaskRepository) GetTasksCreatedBefore(ctx context.Context, before time.Time) ([]*task.Task, error) {
	// This is a more expensive operation as we need to scan all tasks
	// In a production system, you might want to add a time-based index

	pattern := "task:*"
	var cursor uint64
	var tasks []*task.Task

	for {
		keys, nextCursor, err := r.client.Scan(ctx, cursor, pattern, 100).Result()
		if err != nil {
			return nil, fmt.Errorf("failed to scan tasks: %w", err)
		}

		for _, key := range keys {
			// Skip index keys
			if key == "task:type:" || key == "task:status:" || key == "task:priority:" {
				continue
			}

			data, err := r.client.Get(ctx, key).Result()
			if err != nil {
				continue
			}

			var t task.Task
			err = json.Unmarshal([]byte(data), &t)
			if err != nil {
				continue
			}

			if t.CreatedAt.Before(before) {
				tasks = append(tasks, &t)
			}
		}

		cursor = nextCursor
		if cursor == 0 {
			break
		}
	}

	return tasks, nil
}

func (r *RedisTaskRepository) Count(ctx context.Context) (int, error) {
	pattern := "task:*"
	var cursor uint64
	var count int

	for {
		keys, nextCursor, err := r.client.Scan(ctx, cursor, pattern, 100).Result()
		if err != nil {
			return 0, fmt.Errorf("failed to scan tasks: %w", err)
		}

		// Filter out index keys
		for _, key := range keys {
			if key != "task:type:" && key != "task:status:" && key != "task:priority:" {
				count++
			}
		}

		cursor = nextCursor
		if cursor == 0 {
			break
		}
	}

	return count, nil
}

func (r *RedisTaskRepository) CountByStatus(ctx context.Context, status task.TaskStatus) (int, error) {
	statusKey := fmt.Sprintf("task:status:%s", status)

	count, err := r.client.SCard(ctx, statusKey).Result()
	if err != nil {
		return 0, fmt.Errorf("failed to count tasks by status: %w", err)
	}

	return int(count), nil
}

func (r *RedisTaskRepository) CleanupCompletedTasks(ctx context.Context, before time.Time) error {
	// Get completed tasks created before the specified time
	completedTasks, err := r.GetTasksCreatedBefore(ctx, before)
	if err != nil {
		return fmt.Errorf("failed to get completed tasks: %w", err)
	}

	deletedCount := 0
	for _, t := range completedTasks {
		if t.Status == task.TaskStatusCompleted || t.Status == task.TaskStatusFailed {
			err := r.Delete(ctx, t.ID)
			if err != nil {
				r.logger.Error("Failed to delete completed task",
					zap.String("task_id", t.ID),
					zap.Error(err))
				continue
			}
			deletedCount++
		}
	}

	r.logger.Info("Cleaned up completed tasks",
		zap.Int("deleted_count", deletedCount),
		zap.Time("before", before))

	return nil
}
