package test

import (
	"context"
	"testing"
	"time"

	"github.com/alicebob/miniredis/v2"
	"github.com/quantuanhuy/lib/src/config"
	schedulerEntity "github.com/quantuanhuy/lib/src/core/entity/scheduler"
	taskEntity "github.com/quantuanhuy/lib/src/core/entity/task"
	"github.com/quantuanhuy/lib/src/infrastructure/batch"
	"github.com/quantuanhuy/lib/src/infrastructure/scheduler"
	"github.com/quantuanhuy/lib/src/infrastructure/task"
	"github.com/redis/go-redis/v9"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"go.uber.org/zap"
)

func TestSimplifiedIntegration(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	ctx := context.Background()

	t.Run("Job Repository Integration", func(t *testing.T) {
		jobRepo := scheduler.NewRedisJobRepository(client, logger)

		// Create a job
		job := &schedulerEntity.Job{
			ID:       "integration-job-1",
			Name:     "Integration Job 1",
			Type:     "test",
			Schedule: time.Now().Add(-time.Second), // Should be ready to run
			Status:   schedulerEntity.JobStatusPending,
			Payload: map[string]interface{}{
				"test": "data",
			},
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		// Save job
		err := jobRepo.Save(ctx, job)
		require.NoError(t, err)

		// Get jobs ready to run
		jobsToRun, err := jobRepo.GetJobsToRun(ctx, time.Now())
		require.NoError(t, err)
		assert.Len(t, jobsToRun, 1)
		assert.Equal(t, job.ID, jobsToRun[0].ID)

		// Update job status
		jobsToRun[0].Status = schedulerEntity.JobStatusCompleted
		err = jobRepo.Update(ctx, jobsToRun[0])
		require.NoError(t, err)

		// Verify updated status
		updatedJob, err := jobRepo.GetByID(ctx, job.ID)
		require.NoError(t, err)
		assert.Equal(t, schedulerEntity.JobStatusCompleted, updatedJob.Status)
	})

	t.Run("Task Management Integration", func(t *testing.T) {
		taskRepo := task.NewRedisTaskRepository(client, logger)
		taskQueue := task.NewRedisTaskQueue(client, logger, "integration-queue", 100)

		// Create a task
		testTask := &taskEntity.Task{
			ID:       "integration-task-1",
			Name:     "Integration Task 1",
			Type:     "test",
			Priority: taskEntity.TaskPriorityHigh,
			Status:   taskEntity.TaskStatusPending,
			Payload: map[string]interface{}{
				"test": "data",
			},
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		// Save task
		err := taskRepo.Save(ctx, testTask)
		require.NoError(t, err)

		// Enqueue task
		err = taskQueue.Enqueue(ctx, testTask)
		require.NoError(t, err)

		// Dequeue task
		redisQueue := taskQueue.(*task.RedisTaskQueue)
		dequeuedTask, err := redisQueue.DequeueWithPriority(ctx)
		require.NoError(t, err)
		assert.Equal(t, testTask.ID, dequeuedTask.ID)

		// Update task status
		dequeuedTask.Status = taskEntity.TaskStatusCompleted
		err = taskRepo.Update(ctx, dequeuedTask)
		require.NoError(t, err)

		// Verify updated status
		updatedTask, err := taskRepo.GetByID(ctx, testTask.ID)
		require.NoError(t, err)
		assert.Equal(t, taskEntity.TaskStatusCompleted, updatedTask.Status)
	})

	t.Run("Batch Processing Integration", func(t *testing.T) {
		batchRepo := batch.NewRedisBatchJobRepository(client, logger)

		// Create a batch job
		batchJob := &schedulerEntity.BatchJob{
			ID:             "integration-batch-1",
			Name:           "Integration Batch 1",
			Type:           "test",
			BatchSize:      10,
			TotalItems:     100,
			ProcessedItems: 0,
			Status:         schedulerEntity.JobStatusPending,
			Payload: map[string]interface{}{
				"test": "data",
			},
			CreatedAt: time.Now(),
			UpdatedAt: time.Now(),
		}

		// Save batch job
		err := batchRepo.Save(ctx, batchJob)
		require.NoError(t, err)

		// Get pending batch jobs
		pendingJobs, err := batchRepo.GetByStatus(ctx, schedulerEntity.JobStatusPending)
		require.NoError(t, err)
		assert.Len(t, pendingJobs, 1)
		assert.Equal(t, batchJob.ID, pendingJobs[0].ID)

		// Update batch job progress
		batchJob.Status = schedulerEntity.JobStatusRunning
		batchJob.ProcessedItems = 50
		err = batchRepo.Update(ctx, batchJob)
		require.NoError(t, err)

		// Complete batch job
		batchJob.Status = schedulerEntity.JobStatusCompleted
		batchJob.ProcessedItems = 100
		completedAt := time.Now()
		batchJob.CompletedAt = &completedAt
		err = batchRepo.Update(ctx, batchJob)
		require.NoError(t, err)

		// Verify completed status
		completedJob, err := batchRepo.GetByID(ctx, batchJob.ID)
		require.NoError(t, err)
		assert.Equal(t, schedulerEntity.JobStatusCompleted, completedJob.Status)
		assert.Equal(t, 100, completedJob.ProcessedItems)
		assert.NotNil(t, completedJob.CompletedAt)
	})

	t.Run("Configuration Integration", func(t *testing.T) {
		// Test default configurations
		schedulerConfig := config.DefaultSchedulerConfig()
		batchConfig := config.DefaultBatchConfig()
		queueConfig := config.DefaultTaskQueueConfig()

		// Verify scheduler config
		assert.Greater(t, schedulerConfig.Schedule.MaxConcurrentJobs, 0)
		assert.Greater(t, schedulerConfig.Worker.MaxConcurrentTasks, 0)
		assert.NotEmpty(t, schedulerConfig.Redis.Host)

		// Verify batch config
		assert.Greater(t, batchConfig.MaxConcurrentJobs, 0)
		assert.Greater(t, batchConfig.DefaultBatchSize, 0)
		assert.Greater(t, batchConfig.ProcessTimeout, time.Duration(0))

		// Verify queue config
		assert.Greater(t, queueConfig.MaxQueueSize, 0)
		assert.NotEmpty(t, queueConfig.QueueName)

		// Test using configs with actual components
		taskQueue := task.NewRedisTaskQueue(client, logger, queueConfig.QueueName, queueConfig.MaxQueueSize)
		size, err := taskQueue.Size(ctx)
		require.NoError(t, err)
		assert.Equal(t, 0, size)
	})
}

func TestTaskJobBatchInteraction(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	ctx := context.Background()

	t.Run("Task and Job Repository Interaction", func(t *testing.T) {
		jobRepo := scheduler.NewRedisJobRepository(client, logger)
		taskRepo := task.NewRedisTaskRepository(client, logger)

		// Create a job
		job := &schedulerEntity.Job{
			ID:         "interaction-job-1",
			Name:       "Interaction Job 1",
			Type:       "email",
			Schedule:   time.Now().Add(time.Hour),
			Status:     schedulerEntity.JobStatusPending,
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		// Create a task
		task := &taskEntity.Task{
			ID:         "interaction-task-1",
			Name:       "Interaction Task 1",
			Type:       "email",
			Priority:   taskEntity.TaskPriorityHigh,
			Status:     taskEntity.TaskStatusPending,
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		// Save both
		err := jobRepo.Save(ctx, job)
		require.NoError(t, err)

		err = taskRepo.Save(ctx, task)
		require.NoError(t, err)

		// Get by type
		emailJobs, err := jobRepo.GetJobsByType(ctx, "email")
		require.NoError(t, err)
		assert.Len(t, emailJobs, 1)
		assert.Equal(t, job.ID, emailJobs[0].ID)

		emailTasks, err := taskRepo.GetTasksByType(ctx, "email")
		require.NoError(t, err)
		assert.Len(t, emailTasks, 1)
		assert.Equal(t, task.ID, emailTasks[0].ID)

		// Different types should not interfere
		assert.NotEqual(t, job.ID, task.ID)
		assert.Equal(t, job.Type, task.Type)
	})

	t.Run("Priority Queue with Multiple Tasks", func(t *testing.T) {
		taskQueue := task.NewRedisTaskQueue(client, logger, "multi-priority-queue", 100)

		// Create tasks with different priorities
		highTask := &taskEntity.Task{
			ID:        "high-priority-multi",
			Priority:  taskEntity.TaskPriorityHigh,
			Status:    taskEntity.TaskStatusPending,
			CreatedAt: time.Now(),
		}

		normalTask := &taskEntity.Task{
			ID:        "normal-priority-multi",
			Priority:  taskEntity.TaskPriorityNormal,
			Status:    taskEntity.TaskStatusPending,
			CreatedAt: time.Now().Add(time.Millisecond),
		}

		lowTask := &taskEntity.Task{
			ID:        "low-priority-multi",
			Priority:  taskEntity.TaskPriorityLow,
			Status:    taskEntity.TaskStatusPending,
			CreatedAt: time.Now().Add(time.Millisecond * 2),
		}

		// Enqueue in reverse priority order
		err := taskQueue.Enqueue(ctx, lowTask)
		require.NoError(t, err)

		err = taskQueue.Enqueue(ctx, normalTask)
		require.NoError(t, err)

		err = taskQueue.Enqueue(ctx, highTask)
		require.NoError(t, err)

		// Verify queue size
		size, err := taskQueue.Size(ctx)
		require.NoError(t, err)
		assert.Equal(t, 3, size)

		// Dequeue should return in priority order
		redisQueue := taskQueue.(*task.RedisTaskQueue)

		first, err := redisQueue.DequeueWithPriority(ctx)
		require.NoError(t, err)
		assert.Equal(t, taskEntity.TaskPriorityHigh, first.Priority)

		second, err := redisQueue.DequeueWithPriority(ctx)
		require.NoError(t, err)
		assert.Equal(t, taskEntity.TaskPriorityNormal, second.Priority)

		third, err := redisQueue.DequeueWithPriority(ctx)
		require.NoError(t, err)
		assert.Equal(t, taskEntity.TaskPriorityLow, third.Priority)

		// Queue should be empty
		size, err = taskQueue.Size(ctx)
		require.NoError(t, err)
		assert.Equal(t, 0, size)
	})
}
