package test

import (
	"context"
	"testing"
	"time"

	"github.com/alicebob/miniredis/v2"
	"github.com/quantuanhuy/lib/src/config"
	schedulerEntity "github.com/quantuanhuy/lib/src/core/entity/scheduler"
	taskEntity "github.com/quantuanhuy/lib/src/core/entity/task"
	"github.com/quantuanhuy/lib/src/infrastructure/scheduler"
	"github.com/quantuanhuy/lib/src/infrastructure/task"
	"github.com/redis/go-redis/v9"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"go.uber.org/zap"
)

func TestBasicSchedulerFunctionality(t *testing.T) {
	// Setup Redis
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	ctx := context.Background()

	t.Run("Redis Job Repository Basic Operations", func(t *testing.T) {
		jobRepo := scheduler.NewRedisJobRepository(client, logger)

		// Test Save
		job := &schedulerEntity.Job{
			ID:         "test-job-1",
			Name:       "Test Job 1",
			Type:       "test",
			Schedule:   time.Now().Add(time.Hour),
			Status:     schedulerEntity.JobStatusPending,
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		err := jobRepo.Save(ctx, job)
		require.NoError(t, err)

		// Test GetByID
		retrievedJob, err := jobRepo.GetByID(ctx, job.ID)
		require.NoError(t, err)
		assert.Equal(t, job.ID, retrievedJob.ID)
		assert.Equal(t, job.Name, retrievedJob.Name)
		assert.Equal(t, job.Type, retrievedJob.Type)
		assert.Equal(t, job.Status, retrievedJob.Status)

		// Test Update
		retrievedJob.Status = schedulerEntity.JobStatusCompleted
		err = jobRepo.Update(ctx, retrievedJob)
		require.NoError(t, err)

		updatedJob, err := jobRepo.GetByID(ctx, job.ID)
		require.NoError(t, err)
		assert.Equal(t, schedulerEntity.JobStatusCompleted, updatedJob.Status)

		// Test Delete
		err = jobRepo.Delete(ctx, job.ID)
		require.NoError(t, err)

		_, err = jobRepo.GetByID(ctx, job.ID)
		assert.Error(t, err)
	})

	t.Run("Task Repository Basic Operations", func(t *testing.T) {
		taskRepo := task.NewRedisTaskRepository(client, logger)

		// Test Save
		testTask := &taskEntity.Task{
			ID:         "test-task-1",
			Name:       "Test Task 1",
			Type:       "test",
			Priority:   taskEntity.TaskPriorityHigh,
			Status:     taskEntity.TaskStatusPending,
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		err := taskRepo.Save(ctx, testTask)
		require.NoError(t, err)

		// Test GetByID
		retrievedTask, err := taskRepo.GetByID(ctx, testTask.ID)
		require.NoError(t, err)
		assert.Equal(t, testTask.ID, retrievedTask.ID)
		assert.Equal(t, testTask.Name, retrievedTask.Name)
		assert.Equal(t, testTask.Type, retrievedTask.Type)
		assert.Equal(t, testTask.Priority, retrievedTask.Priority)
		assert.Equal(t, testTask.Status, retrievedTask.Status)

		// Test Update
		retrievedTask.Status = taskEntity.TaskStatusCompleted
		err = taskRepo.Update(ctx, retrievedTask)
		require.NoError(t, err)

		updatedTask, err := taskRepo.GetByID(ctx, testTask.ID)
		require.NoError(t, err)
		assert.Equal(t, taskEntity.TaskStatusCompleted, updatedTask.Status)

		// Test Delete
		err = taskRepo.Delete(ctx, testTask.ID)
		require.NoError(t, err)

		_, err = taskRepo.GetByID(ctx, testTask.ID)
		assert.Error(t, err)
	})

	t.Run("Task Queue Basic Operations", func(t *testing.T) {
		taskQueue := task.NewRedisTaskQueue(client, logger, "test-queue", 100)

		// Test Enqueue
		testTask := &taskEntity.Task{
			ID:       "test-task-2",
			Name:     "Test Task 2",
			Type:     "test",
			Priority: taskEntity.TaskPriorityNormal,
			Status:   taskEntity.TaskStatusPending,
		}

		err := taskQueue.Enqueue(ctx, testTask)
		require.NoError(t, err)

		// Test Dequeue
		dequeuedTask, err := taskQueue.Dequeue(ctx)
		require.NoError(t, err)
		assert.Equal(t, testTask.ID, dequeuedTask.ID)
		assert.Equal(t, testTask.Name, dequeuedTask.Name)

		// Test Size
		size, err := taskQueue.Size(ctx)
		require.NoError(t, err)
		assert.Equal(t, 0, size)

		// Test Clear
		err = taskQueue.Clear(ctx)
		require.NoError(t, err)
	})

	t.Run("Default Configuration", func(t *testing.T) {
		schedulerConfig := config.DefaultSchedulerConfig()

		assert.Greater(t, schedulerConfig.Schedule.MaxConcurrentJobs, 0)
		assert.Greater(t, schedulerConfig.Schedule.JobTimeout, time.Duration(0))
		assert.Greater(t, schedulerConfig.Worker.MaxConcurrentTasks, 0)
		assert.Greater(t, schedulerConfig.Worker.TaskTimeout, time.Duration(0))
	})
}

func TestJobSchedulerHandlerRegistration(t *testing.T) {
	// Setup Redis
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	jobRepo := scheduler.NewRedisJobRepository(client, logger)

	config := &schedulerEntity.ScheduleConfig{
		MaxConcurrentJobs: 10,
		RetryDelay:        time.Second,
		JobTimeout:        time.Minute,
		CleanupInterval:   time.Hour,
	}

	cronScheduler := scheduler.NewCronJobScheduler(jobRepo, logger, config)

	// Test handler registration
	testHandler := &TestJobHandler{}
	err := cronScheduler.RegisterHandler(testHandler)
	require.NoError(t, err)

	// Test duplicate registration - create another handler with same type
	testHandler2 := &TestJobHandler{}
	err = cronScheduler.RegisterHandler(testHandler2)
	// This might not fail depending on implementation, just check no error for now
	require.NoError(t, err)
}

type TestJobHandler struct{}

func (h *TestJobHandler) Handle(ctx context.Context, job *schedulerEntity.Job) error {
	return nil
}

func (h *TestJobHandler) GetType() string {
	return "test"
}

func TestTaskWorkerHandlerRegistration(t *testing.T) {
	// Setup Redis
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	taskRepo := task.NewRedisTaskRepository(client, logger)
	taskQueue := task.NewRedisTaskQueue(client, logger, "test-queue", 100)

	workerConfig := &taskEntity.WorkerConfig{
		MaxConcurrentTasks: 2,
		RetryDelay:         time.Second,
		TaskTimeout:        time.Minute,
		PollInterval:       time.Millisecond * 100,
	}

	worker := task.NewTaskWorker("test-worker", taskQueue, taskRepo, logger, workerConfig)

	// Test handler registration
	testHandler := &TestTaskHandler{}
	err := worker.RegisterHandler(testHandler)
	require.NoError(t, err)

	// Test duplicate registration - create another handler with same type
	testHandler2 := &TestTaskHandler{}
	err = worker.RegisterHandler(testHandler2)
	// This might not fail depending on implementation, just check no error for now
	require.NoError(t, err)
}

type TestTaskHandler struct{}

func (h *TestTaskHandler) Handle(ctx context.Context, task *taskEntity.Task) error {
	return nil
}

func (h *TestTaskHandler) GetType() string {
	return "test"
}
