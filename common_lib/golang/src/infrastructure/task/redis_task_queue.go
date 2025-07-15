package task

import (
	"context"
	"encoding/json"
	"fmt"

	"github.com/quantuanhuy/lib/src/core/entity/task"
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

func NewRedisTaskQueue(client *redis.Client, logger *zap.Logger, queueName string, maxSize int) port.TaskQueue {
	return &RedisTaskQueue{
		client:    client,
		logger:    logger,
		queueName: queueName,
		maxSize:   maxSize,
	}
}

func (q *RedisTaskQueue) Enqueue(ctx context.Context, task *task.Task) error {
	// Check queue size
	size, err := q.Size(ctx)
	if err != nil {
		return fmt.Errorf("failed to get queue size: %w", err)
	}

	if q.maxSize > 0 && size >= q.maxSize {
		return fmt.Errorf("queue is full (max size: %d)", q.maxSize)
	}

	// Serialize task
	data, err := json.Marshal(task)
	if err != nil {
		return fmt.Errorf("failed to marshal task: %w", err)
	}

	// Add to queue with priority score
	score := float64(task.Priority)*1000000 + float64(task.CreatedAt.Unix())
	err = q.client.ZAdd(ctx, q.queueName, redis.Z{
		Score:  score,
		Member: string(data),
	}).Err()

	if err != nil {
		return fmt.Errorf("failed to enqueue task: %w", err)
	}

	q.logger.Debug("Task enqueued",
		zap.String("task_id", task.ID),
		zap.String("queue", q.queueName),
		zap.Int("priority", int(task.Priority)))

	return nil
}

func (q *RedisTaskQueue) Dequeue(ctx context.Context) (*task.Task, error) {
	// Get task with lowest score (FIFO)
	results, err := q.client.ZPopMin(ctx, q.queueName, 1).Result()
	if err != nil {
		if err == redis.Nil {
			return nil, nil // Queue is empty
		}
		return nil, fmt.Errorf("failed to dequeue task: %w", err)
	}

	if len(results) == 0 {
		return nil, nil // Queue is empty
	}

	// Deserialize task
	var task task.Task
	err = json.Unmarshal([]byte(results[0].Member.(string)), &task)
	if err != nil {
		return nil, fmt.Errorf("failed to unmarshal task: %w", err)
	}

	q.logger.Debug("Task dequeued",
		zap.String("task_id", task.ID),
		zap.String("queue", q.queueName))

	return &task, nil
}

func (q *RedisTaskQueue) DequeueWithPriority(ctx context.Context) (*task.Task, error) {
	// Get task with highest priority (highest score)
	results, err := q.client.ZPopMax(ctx, q.queueName, 1).Result()
	if err != nil {
		if err == redis.Nil {
			return nil, nil // Queue is empty
		}
		return nil, fmt.Errorf("failed to dequeue task: %w", err)
	}

	if len(results) == 0 {
		return nil, nil // Queue is empty
	}

	// Deserialize task
	var task task.Task
	err = json.Unmarshal([]byte(results[0].Member.(string)), &task)
	if err != nil {
		return nil, fmt.Errorf("failed to unmarshal task: %w", err)
	}

	q.logger.Debug("Task dequeued with priority",
		zap.String("task_id", task.ID),
		zap.String("queue", q.queueName),
		zap.Int("priority", int(task.Priority)))

	return &task, nil
}

func (q *RedisTaskQueue) Size(ctx context.Context) (int, error) {
	size, err := q.client.ZCard(ctx, q.queueName).Result()
	if err != nil {
		return 0, fmt.Errorf("failed to get queue size: %w", err)
	}
	return int(size), nil
}

func (q *RedisTaskQueue) IsEmpty(ctx context.Context) (bool, error) {
	size, err := q.Size(ctx)
	if err != nil {
		return false, err
	}
	return size == 0, nil
}

func (q *RedisTaskQueue) Clear(ctx context.Context) error {
	err := q.client.Del(ctx, q.queueName).Err()
	if err != nil {
		return fmt.Errorf("failed to clear queue: %w", err)
	}

	q.logger.Info("Queue cleared", zap.String("queue", q.queueName))
	return nil
}
