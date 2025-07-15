package task

import (
	"context"
	"fmt"
	"sync"
	"testing"
	"time"

	"github.com/alicebob/miniredis/v2"
	taskEntity "github.com/quantuanhuy/lib/src/core/entity/task"
	"github.com/redis/go-redis/v9"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"go.uber.org/zap"
)

// Mock task handler for testing
type MockTaskHandler struct {
	processed []string
	shouldErr bool
	delay     time.Duration
	mu        sync.Mutex
}

func (h *MockTaskHandler) Handle(ctx context.Context, task *taskEntity.Task) error {
	h.mu.Lock()
	defer h.mu.Unlock()

	if h.delay > 0 {
		time.Sleep(h.delay)
	}

	h.processed = append(h.processed, task.ID)
	if h.shouldErr {
		return assert.AnError
	}
	return nil
}

func (h *MockTaskHandler) GetType() string {
	return "mock"
}

func (h *MockTaskHandler) GetProcessed() []string {
	h.mu.Lock()
	defer h.mu.Unlock()
	return append([]string{}, h.processed...)
}

func TestTaskWorker_RegisterHandler(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	repo := NewRedisTaskRepository(client, logger)
	queue := NewRedisTaskQueue(client, logger, "test-queue", 100)
	config := &taskEntity.WorkerConfig{
		MaxConcurrentTasks: 2,
		RetryDelay:         time.Second,
		TaskTimeout:        time.Minute,
		PollInterval:       time.Millisecond * 100,
	}

	worker := NewTaskWorker("test-worker", queue, repo, logger, config)

	handler := &MockTaskHandler{}
	err := worker.RegisterHandler(handler)
	require.NoError(t, err)
}

func TestTaskWorker_ProcessTask(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	repo := NewRedisTaskRepository(client, logger)
	queue := NewRedisTaskQueue(client, logger, "test-queue", 100)
	config := &taskEntity.WorkerConfig{
		MaxConcurrentTasks: 2,
		RetryDelay:         time.Second,
		TaskTimeout:        time.Minute,
		PollInterval:       time.Millisecond * 100,
	}

	worker := NewTaskWorker("test-worker", queue, repo, logger, config)
	handler := &MockTaskHandler{}
	err := worker.RegisterHandler(handler)
	require.NoError(t, err)

	ctx := context.Background()

	// Create and enqueue a task
	testTask := &taskEntity.Task{
		ID:         "test-task-1",
		Name:       "Test Task",
		Type:       "mock",
		Priority:   taskEntity.TaskPriorityNormal,
		Status:     taskEntity.TaskStatusPending,
		MaxRetries: 3,
		CreatedAt:  time.Now(),
		UpdatedAt:  time.Now(),
	}

	err = queue.Enqueue(ctx, testTask)
	require.NoError(t, err)

	// Start worker
	err = worker.Start(ctx)
	require.NoError(t, err)

	// Wait for task to be processed
	time.Sleep(time.Millisecond * 500)

	// Stop worker
	err = worker.Stop(ctx)
	require.NoError(t, err)

	// Verify task was processed
	processed := handler.GetProcessed()
	assert.Contains(t, processed, "test-task-1")

	// Verify task status was updated
	retrievedTask, err := repo.GetByID(ctx, testTask.ID)
	require.NoError(t, err)
	assert.Equal(t, taskEntity.TaskStatusCompleted, retrievedTask.Status)
}

func TestTaskWorker_ProcessTaskWithError(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	repo := NewRedisTaskRepository(client, logger)
	queue := NewRedisTaskQueue(client, logger, "test-queue", 100)
	config := &taskEntity.WorkerConfig{
		MaxConcurrentTasks: 2,
		RetryDelay:         time.Millisecond * 100,
		TaskTimeout:        time.Minute,
		PollInterval:       time.Millisecond * 100,
	}

	worker := NewTaskWorker("test-worker", queue, repo, logger, config)
	handler := &MockTaskHandler{shouldErr: true}
	err := worker.RegisterHandler(handler)
	require.NoError(t, err)

	ctx := context.Background()

	// Create and enqueue a task
	testTask := &taskEntity.Task{
		ID:         "test-task-1",
		Name:       "Test Task",
		Type:       "mock",
		Priority:   taskEntity.TaskPriorityNormal,
		Status:     taskEntity.TaskStatusPending,
		MaxRetries: 2,
		CreatedAt:  time.Now(),
		UpdatedAt:  time.Now(),
	}

	err = queue.Enqueue(ctx, testTask)
	require.NoError(t, err)

	// Start worker
	err = worker.Start(ctx)
	require.NoError(t, err)

	// Wait for task to be processed and retried
	time.Sleep(time.Millisecond * 800)

	// Stop worker
	err = worker.Stop(ctx)
	require.NoError(t, err)

	// Verify task was processed multiple times (retries)
	processed := handler.GetProcessed()
	assert.Contains(t, processed, "test-task-1")

	// Verify task status is failed after max retries
	retrievedTask, err := repo.GetByID(ctx, testTask.ID)
	require.NoError(t, err)
	assert.Equal(t, taskEntity.TaskStatusFailed, retrievedTask.Status)
	assert.Greater(t, retrievedTask.Retries, 0)
}

func TestTaskWorker_GetStats(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	repo := NewRedisTaskRepository(client, logger)
	queue := NewRedisTaskQueue(client, logger, "test-queue", 100)
	config := &taskEntity.WorkerConfig{
		MaxConcurrentTasks: 2,
		RetryDelay:         time.Second,
		TaskTimeout:        time.Minute,
		PollInterval:       time.Millisecond * 100,
	}

	worker := NewTaskWorker("test-worker", queue, repo, logger, config)
	handler := &MockTaskHandler{}
	err := worker.RegisterHandler(handler)
	require.NoError(t, err)

	ctx := context.Background()

	// Get initial stats
	stats, err := worker.GetStats(ctx)
	require.NoError(t, err)
	assert.Equal(t, 0, stats.TotalProcessed)
	assert.Equal(t, 0, stats.TotalFailed)
	assert.Equal(t, 0, stats.ActiveTasks)

	// Start worker
	err = worker.Start(ctx)
	require.NoError(t, err)

	// Create and enqueue multiple tasks
	for i := 0; i < 3; i++ {
		testTask := &taskEntity.Task{
			ID:         fmt.Sprintf("test-task-%d", i),
			Name:       fmt.Sprintf("Test Task %d", i),
			Type:       "mock",
			Priority:   taskEntity.TaskPriorityNormal,
			Status:     taskEntity.TaskStatusPending,
			MaxRetries: 1,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		err = queue.Enqueue(ctx, testTask)
		require.NoError(t, err)
	}

	// Wait for tasks to be processed
	time.Sleep(time.Millisecond * 500)

	// Get final stats
	stats, err = worker.GetStats(ctx)
	require.NoError(t, err)
	assert.Equal(t, 3, stats.TotalProcessed)
	assert.Equal(t, 0, stats.TotalFailed)
	assert.Equal(t, 0, stats.ActiveTasks)

	// Stop worker
	err = worker.Stop(ctx)
	require.NoError(t, err)
}
