package task

import (
	"context"
	"fmt"
	"testing"
	"time"

	"github.com/alicebob/miniredis/v2"
	taskEntity "github.com/quantuanhuy/lib/src/core/entity/task"
	"github.com/redis/go-redis/v9"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"go.uber.org/zap"
)

func TestRedisTaskQueue_Enqueue(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	queue := NewRedisTaskQueue(client, logger, "test-queue", 100)

	ctx := context.Background()

	testTask := &taskEntity.Task{
		ID:        "test-task-1",
		Name:      "Test Task",
		Type:      "test",
		Priority:  taskEntity.TaskPriorityHigh,
		Status:    taskEntity.TaskStatusPending,
		CreatedAt: time.Now(),
		UpdatedAt: time.Now(),
	}

	err := queue.Enqueue(ctx, testTask)
	require.NoError(t, err)

	// Check queue size
	size, err := queue.Size(ctx)
	require.NoError(t, err)
	assert.Equal(t, 1, size)

	// Check queue is not empty
	isEmpty, err := queue.IsEmpty(ctx)
	require.NoError(t, err)
	assert.False(t, isEmpty)
}

func TestRedisTaskQueue_Dequeue(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	queue := NewRedisTaskQueue(client, logger, "test-queue", 100)

	ctx := context.Background()

	// Test dequeue from empty queue
	task, err := queue.Dequeue(ctx)
	require.NoError(t, err)
	assert.Nil(t, task)

	// Add task and dequeue
	testTask := &taskEntity.Task{
		ID:        "test-task-1",
		Name:      "Test Task",
		Type:      "test",
		Priority:  taskEntity.TaskPriorityNormal,
		Status:    taskEntity.TaskStatusPending,
		CreatedAt: time.Now(),
		UpdatedAt: time.Now(),
	}

	err = queue.Enqueue(ctx, testTask)
	require.NoError(t, err)

	dequeuedTask, err := queue.Dequeue(ctx)
	require.NoError(t, err)
	assert.NotNil(t, dequeuedTask)
	assert.Equal(t, testTask.ID, dequeuedTask.ID)

	// Queue should be empty now
	isEmpty, err := queue.IsEmpty(ctx)
	require.NoError(t, err)
	assert.True(t, isEmpty)
}

func TestRedisTaskQueue_DequeueWithPriority(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	queue := NewRedisTaskQueue(client, logger, "test-queue", 100)

	ctx := context.Background()

	// Add tasks with different priorities
	tasks := []*taskEntity.Task{
		{
			ID:        "low-priority",
			Name:      "Low Priority Task",
			Type:      "test",
			Priority:  taskEntity.TaskPriorityLow,
			Status:    taskEntity.TaskStatusPending,
			CreatedAt: time.Now(),
			UpdatedAt: time.Now(),
		},
		{
			ID:        "high-priority",
			Name:      "High Priority Task",
			Type:      "test",
			Priority:  taskEntity.TaskPriorityHigh,
			Status:    taskEntity.TaskStatusPending,
			CreatedAt: time.Now(),
			UpdatedAt: time.Now(),
		},
		{
			ID:        "normal-priority",
			Name:      "Normal Priority Task",
			Type:      "test",
			Priority:  taskEntity.TaskPriorityNormal,
			Status:    taskEntity.TaskStatusPending,
			CreatedAt: time.Now(),
			UpdatedAt: time.Now(),
		},
	}

	for _, task := range tasks {
		err := queue.Enqueue(ctx, task)
		require.NoError(t, err)
	}

	// Dequeue with priority - should get highest priority first
	dequeuedTask, err := queue.DequeueWithPriority(ctx)
	require.NoError(t, err)
	assert.Equal(t, "high-priority", dequeuedTask.ID)

	// Next should be normal priority
	dequeuedTask, err = queue.DequeueWithPriority(ctx)
	require.NoError(t, err)
	assert.Equal(t, "normal-priority", dequeuedTask.ID)

	// Last should be low priority
	dequeuedTask, err = queue.DequeueWithPriority(ctx)
	require.NoError(t, err)
	assert.Equal(t, "low-priority", dequeuedTask.ID)
}

func TestRedisTaskQueue_MaxSize(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	queue := NewRedisTaskQueue(client, logger, "test-queue", 2) // Max size 2

	ctx := context.Background()

	// Add tasks up to max size
	for i := 0; i < 2; i++ {
		testTask := &taskEntity.Task{
			ID:        fmt.Sprintf("task-%d", i),
			Name:      fmt.Sprintf("Task %d", i),
			Type:      "test",
			Priority:  taskEntity.TaskPriorityNormal,
			Status:    taskEntity.TaskStatusPending,
			CreatedAt: time.Now(),
			UpdatedAt: time.Now(),
		}

		err := queue.Enqueue(ctx, testTask)
		require.NoError(t, err)
	}

	// Try to add one more - should fail
	overflowTask := &taskEntity.Task{
		ID:        "overflow-task",
		Name:      "Overflow Task",
		Type:      "test",
		Priority:  taskEntity.TaskPriorityNormal,
		Status:    taskEntity.TaskStatusPending,
		CreatedAt: time.Now(),
		UpdatedAt: time.Now(),
	}

	err := queue.Enqueue(ctx, overflowTask)
	assert.Error(t, err)
	assert.Contains(t, err.Error(), "queue is full")
}

func TestRedisTaskQueue_Clear(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	queue := NewRedisTaskQueue(client, logger, "test-queue", 100)

	ctx := context.Background()

	// Add some tasks
	for i := 0; i < 3; i++ {
		testTask := &taskEntity.Task{
			ID:        fmt.Sprintf("task-%d", i),
			Name:      fmt.Sprintf("Task %d", i),
			Type:      "test",
			Priority:  taskEntity.TaskPriorityNormal,
			Status:    taskEntity.TaskStatusPending,
			CreatedAt: time.Now(),
			UpdatedAt: time.Now(),
		}

		err := queue.Enqueue(ctx, testTask)
		require.NoError(t, err)
	}

	// Verify queue has items
	size, err := queue.Size(ctx)
	require.NoError(t, err)
	assert.Equal(t, 3, size)

	// Clear queue
	err = queue.Clear(ctx)
	require.NoError(t, err)

	// Verify queue is empty
	isEmpty, err := queue.IsEmpty(ctx)
	require.NoError(t, err)
	assert.True(t, isEmpty)

	size, err = queue.Size(ctx)
	require.NoError(t, err)
	assert.Equal(t, 0, size)
}
