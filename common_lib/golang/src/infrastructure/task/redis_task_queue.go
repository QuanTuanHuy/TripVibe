package task

import (
	"context"
	"encoding/json"
	"fmt"

	entity "github.com/quantuanhuy/lib/src/core/entity/task"
	"github.com/quantuanhuy/lib/src/core/port"
	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"
)

type RedisTaskQueue struct {
	client    *redis.Client
	logger    *zap.Logger
	queueName string
	maxSize   int
}

func (r *RedisTaskQueue) Enqueue(ctx context.Context, task *entity.Task) error {
	size, err := r.Size(ctx)
	if err != nil {
		return fmt.Errorf("failed to get queue size: %w", err)
	}
	if r.maxSize > 0 && size >= r.maxSize {
		return fmt.Errorf("queue is full, cannot enqueue task: %s", task.ID)
	}

	data, err := json.Marshal(task)
	if err != nil {
		return fmt.Errorf("failed to marshal task: %w", err)
	}

	score := float64(task.Priority)*1000000 + float64(task.CreatedAt.Unix())
	err = r.client.ZAdd(ctx, r.queueName, redis.Z{
		Score:  score,
		Member: string(data),
	}).Err()
	if err != nil {
		return fmt.Errorf("failed to enqueue task: %w", err)
	}

	r.logger.Debug("Task enqueued",
		zap.String("queue_name", r.queueName),
		zap.String("task_id", task.ID),
		zap.Int("queue_size", size+1))

	return nil
}

func (r *RedisTaskQueue) Dequeue(ctx context.Context) (*entity.Task, error) {
	results, err := r.client.ZPopMin(ctx, r.queueName, 1).Result()
	if err != nil {
		if err == redis.Nil {
			return nil, nil // Queue is empty
		}
		return nil, fmt.Errorf("failed to dequeue task: %w", err)
	}
	if len(results) == 0 {
		return nil, nil // Queue is empty
	}

	var task entity.Task
	if err := json.Unmarshal([]byte(results[0].Member.(string)), &task); err != nil {
		return nil, fmt.Errorf("failed to unmarshal task: %w", err)
	}

	r.logger.Debug("Task dequeued",
		zap.String("queue_name", r.queueName),
		zap.String("task_id", task.ID))
	return &task, nil
}

func (r *RedisTaskQueue) DequeueWithPriority(ctx context.Context) (*entity.Task, error) {
	results, err := r.client.ZPopMax(ctx, r.queueName, 1).Result()
	if err != nil {
		if err == redis.Nil {
			return nil, nil
		}
		return nil, fmt.Errorf("failed to dequeue task with priority: %w", err)
	}
	if len(results) == 0 {
		return nil, nil
	}

	var task entity.Task
	if err := json.Unmarshal([]byte(results[0].Member.(string)), &task); err != nil {
		return nil, fmt.Errorf("failed to unmarshal task: %w", err)
	}
	r.logger.Debug("Task dequeued with priority",
		zap.String("queue_name", r.queueName),
		zap.String("task_id", task.ID))

	return &task, nil
}

func (r *RedisTaskQueue) Size(ctx context.Context) (int, error) {
	size, err := r.client.ZCard(ctx, r.queueName).Result()
	if err != nil {
		return 0, fmt.Errorf("failed to get queue size: %w", err)
	}
	return int(size), nil
}

func (r *RedisTaskQueue) Clear(ctx context.Context) error {
	err := r.client.Del(ctx, r.queueName).Err()
	if err != nil {
		return fmt.Errorf("failed to clear queue: %w", err)
	}
	
	r.logger.Debug("Queue cleared",
		zap.String("queue_name", r.queueName))
	return nil
}

func (r *RedisTaskQueue) IsEmpty(ctx context.Context) (bool, error) {
	size, err := r.Size(ctx)
	if err != nil {
		return false, fmt.Errorf("failed to check if queue is empty: %w", err)
	}
	return size == 0, nil
}

func NewRedisTaskQueue(client *redis.Client, logger *zap.Logger, queueName string, maxSize int) port.ITaskQueuePort {
	return &RedisTaskQueue{
		client:    client,
		logger:    logger,
		queueName: queueName,
		maxSize:   maxSize,
	}
}
