package test

import (
	"context"
	"testing"
	"time"

	"github.com/alicebob/miniredis/v2"
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

// Mock handlers for integration testing
type EmailJobHandler struct {
	processed []string
	logger    *zap.Logger
}

func (h *EmailJobHandler) Handle(ctx context.Context, job *schedulerEntity.Job) error {
	h.processed = append(h.processed, job.ID)
	h.logger.Info("Email job processed", zap.String("job_id", job.ID))
	return nil
}

func (h *EmailJobHandler) GetType() string {
	return "email"
}

type NotificationTaskHandler struct {
	processed []string
	logger    *zap.Logger
}

func (h *NotificationTaskHandler) Handle(ctx context.Context, task *taskEntity.Task) error {
	h.processed = append(h.processed, task.ID)
	h.logger.Info("Notification task processed", zap.String("task_id", task.ID))
	return nil
}

func (h *NotificationTaskHandler) GetType() string {
	return "notification"
}

type DataBatchProcessor struct {
	processed []string
	logger    *zap.Logger
}

func (p *DataBatchProcessor) ProcessBatch(ctx context.Context, job *schedulerEntity.BatchJob, items []interface{}) error {
	p.processed = append(p.processed, job.ID)
	p.logger.Info("Batch processed",
		zap.String("job_id", job.ID),
		zap.Int("items_count", len(items)))
	return nil
}

func (p *DataBatchProcessor) GetType() string {
	return "data_processing"
}

func TestSchedulerIntegration(t *testing.T) {
	// Setup Redis
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()

	// Setup scheduler components
	jobRepo := scheduler.NewRedisJobRepository(client, logger)
	config := &schedulerEntity.ScheduleConfig{
		MaxConcurrentJobs: 10,
		RetryDelay:        time.Second,
		JobTimeout:        time.Minute,
		CleanupInterval:   time.Hour,
	}

	cronScheduler := scheduler.NewCronJobScheduler(jobRepo, logger, config)

	// Register handler
	emailHandler := &EmailJobHandler{logger: logger}
	err := cronScheduler.RegisterHandler(emailHandler)
	require.NoError(t, err)

	ctx := context.Background()

	// Start scheduler
	err = cronScheduler.Start(ctx)
	require.NoError(t, err)
	defer cronScheduler.Stop(ctx)

	// Schedule a job
	job := &schedulerEntity.Job{
		ID:         "integration-email-job",
		Name:       "Integration Email Job",
		Type:       "email",
		Schedule:   time.Now().Add(time.Millisecond * 100),
		MaxRetries: 3,
		Payload: map[string]interface{}{
			"to":      "test@example.com",
			"subject": "Test Email",
			"body":    "This is a test email",
		},
	}

	err = cronScheduler.ScheduleJob(ctx, job)
	require.NoError(t, err)

	// Wait for job to be processed with polling
	maxWait := time.Second * 5
	pollInterval := time.Millisecond * 100
	deadline := time.Now().Add(maxWait)

	t.Logf("Waiting for job %s to be processed...", job.ID)
	for time.Now().Before(deadline) {
		if len(emailHandler.processed) >= 1 {
			t.Logf("Job processed by handler")
			break
		}
		time.Sleep(pollInterval)
	}

	// Check if job was actually scheduled
	savedJob, err := cronScheduler.GetJobStatus(ctx, job.ID)
	require.NoError(t, err, "Should be able to get job from repository")
	require.NotNil(t, savedJob, "Job should exist in repository")

	t.Logf("Job found in repository with status: %s", savedJob.Status)

	// Verify job was processed
	if !assert.Contains(t, emailHandler.processed, job.ID) {
		t.Logf("Handler processed jobs: %v", emailHandler.processed)
		t.Logf("Expected job ID: %s", job.ID)
	}

	// Since we already have the job, just verify its status
	if savedJob.Status == schedulerEntity.JobStatusCompleted {
		assert.Equal(t, schedulerEntity.JobStatusCompleted, savedJob.Status)
	} else {
		// If not completed yet, poll for completion
		var retrievedJob *schedulerEntity.Job
		deadline = time.Now().Add(maxWait)

		for time.Now().Before(deadline) {
			retrievedJob, jobErr := cronScheduler.GetJobStatus(ctx, job.ID)
			if jobErr != nil {
				t.Logf("Error getting job status: %v", jobErr)
				time.Sleep(pollInterval)
				continue
			}
			if retrievedJob != nil && retrievedJob.Status == schedulerEntity.JobStatusCompleted {
				break
			}
			if retrievedJob != nil {
				t.Logf("Job status: %s", retrievedJob.Status)
			}
			time.Sleep(pollInterval)
		}

		require.NotNil(t, retrievedJob, "Job should exist in repository")
		assert.Equal(t, schedulerEntity.JobStatusCompleted, retrievedJob.Status)
	}
}

func TestTaskManagerIntegration(t *testing.T) {
	// Setup Redis
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()

	// Setup task components
	taskRepo := task.NewRedisTaskRepository(client, logger)
	taskQueue := task.NewRedisTaskQueue(client, logger, "integration-queue", 100)

	workerConfig := &taskEntity.WorkerConfig{
		MaxConcurrentTasks: 2,
		RetryDelay:         time.Second,
		TaskTimeout:        time.Minute,
		PollInterval:       time.Millisecond * 100,
	}

	worker := task.NewTaskWorker("integration-worker", taskQueue, taskRepo, logger, workerConfig)
	taskManager := task.NewTaskManager(taskQueue, taskRepo, worker, logger)

	// Register handler
	notificationHandler := &NotificationTaskHandler{logger: logger}
	err := worker.RegisterHandler(notificationHandler)
	require.NoError(t, err)

	ctx := context.Background()

	// Start task manager
	err = taskManager.Start(ctx)
	require.NoError(t, err)
	defer taskManager.Stop(ctx)

	// Submit a task
	testTask := &taskEntity.Task{
		ID:         "integration-notification-task",
		Name:       "Integration Notification Task",
		Type:       "notification",
		Priority:   taskEntity.TaskPriorityHigh,
		MaxRetries: 2,
		Payload: map[string]interface{}{
			"user_id": "user123",
			"message": "Integration test notification",
		},
	}

	err = taskManager.SubmitTask(ctx, testTask)
	require.NoError(t, err)

	// Wait for task to be processed with polling
	maxWait := time.Second * 5
	pollInterval := time.Millisecond * 100
	deadline := time.Now().Add(maxWait)

	for time.Now().Before(deadline) {
		if len(notificationHandler.processed) >= 1 {
			break
		}
		time.Sleep(pollInterval)
	}

	// Verify task was processed
	assert.Contains(t, notificationHandler.processed, testTask.ID)

	// Verify task status with polling
	var retrievedTask *taskEntity.Task
	deadline = time.Now().Add(maxWait)

	// First check if task exists
	retrievedTask, taskErr := taskManager.GetTaskStatus(ctx, testTask.ID)
	require.NoError(t, taskErr, "Should be able to get task from repository")
	require.NotNil(t, retrievedTask, "Task should exist in repository")

	// If not completed yet, poll for completion
	if retrievedTask.Status != taskEntity.TaskStatusCompleted {
		for time.Now().Before(deadline) {
			retrievedTask, taskErr = taskManager.GetTaskStatus(ctx, testTask.ID)
			if taskErr != nil {
				t.Logf("Error getting task status: %v", taskErr)
				time.Sleep(pollInterval)
				continue
			}
			if retrievedTask != nil && retrievedTask.Status == taskEntity.TaskStatusCompleted {
				break
			}
			time.Sleep(pollInterval)
		}
	}

	require.NotNil(t, retrievedTask)
	assert.Equal(t, taskEntity.TaskStatusCompleted, retrievedTask.Status)
}

func TestBatchProcessorIntegration(t *testing.T) {
	// Setup Redis
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()

	// Setup batch components
	batchRepo := batch.NewRedisBatchJobRepository(client, logger)
	batchProcessor := batch.NewBatchProcessor(batchRepo, logger)

	// Register processor
	dataProcessor := &DataBatchProcessor{logger: logger}
	err := batchProcessor.RegisterProcessor(dataProcessor)
	require.NoError(t, err)

	ctx := context.Background()

	// Start batch processor
	err = batchProcessor.Start(ctx)
	require.NoError(t, err)
	defer batchProcessor.Stop(ctx)

	// Create batch job
	batchJob := &schedulerEntity.BatchJob{
		ID:         "integration-batch-job",
		Name:       "Integration Batch Job",
		Type:       "data_processing",
		BatchSize:  10,
		TotalItems: 100,
		Status:     schedulerEntity.JobStatusPending,
		Payload: map[string]interface{}{
			"source": "integration_test_data",
			"target": "processed_data",
		},
		CreatedAt: time.Now(),
		UpdatedAt: time.Now(),
	}

	err = batchRepo.Save(ctx, batchJob)
	require.NoError(t, err)

	// Wait for batch to be processed with polling
	maxWait := time.Second * 10
	pollInterval := time.Millisecond * 100
	deadline := time.Now().Add(maxWait)

	for time.Now().Before(deadline) {
		if len(dataProcessor.processed) >= 1 {
			break
		}
		time.Sleep(pollInterval)
	}

	// Verify batch was processed
	assert.Contains(t, dataProcessor.processed, batchJob.ID)

	// Verify batch status with polling
	var retrievedJob *schedulerEntity.BatchJob
	deadline = time.Now().Add(maxWait)

	// First check if batch job exists
	retrievedJob, batchErr := batchRepo.GetByID(ctx, batchJob.ID)
	require.NoError(t, batchErr, "Should be able to get batch job from repository")
	require.NotNil(t, retrievedJob, "Batch job should exist in repository")

	// If not completed yet, poll for completion
	if retrievedJob.Status != schedulerEntity.JobStatusCompleted {
		for time.Now().Before(deadline) {
			retrievedJob, batchErr = batchRepo.GetByID(ctx, batchJob.ID)
			if batchErr != nil {
				t.Logf("Error getting batch job status: %v", batchErr)
				time.Sleep(pollInterval)
				continue
			}
			if retrievedJob != nil && retrievedJob.Status == schedulerEntity.JobStatusCompleted {
				break
			}
			time.Sleep(pollInterval)
		}
	}

	require.NotNil(t, retrievedJob)
	assert.Equal(t, schedulerEntity.JobStatusCompleted, retrievedJob.Status)
	assert.Equal(t, 100, retrievedJob.ProcessedItems)
}

func TestFullSystemIntegration(t *testing.T) {
	// Setup Redis
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()

	// Setup all components
	jobRepo := scheduler.NewRedisJobRepository(client, logger)
	taskRepo := task.NewRedisTaskRepository(client, logger)
	batchRepo := batch.NewRedisBatchJobRepository(client, logger)

	taskQueue := task.NewRedisTaskQueue(client, logger, "full-system-queue", 100)

	schedulerConfig := &schedulerEntity.ScheduleConfig{
		MaxConcurrentJobs: 5,
		RetryDelay:        time.Second,
		JobTimeout:        time.Minute,
		CleanupInterval:   time.Hour,
	}

	workerConfig := &taskEntity.WorkerConfig{
		MaxConcurrentTasks: 3,
		RetryDelay:         time.Second,
		TaskTimeout:        time.Minute,
		PollInterval:       time.Millisecond * 100,
	}

	cronScheduler := scheduler.NewCronJobScheduler(jobRepo, logger, schedulerConfig)
	worker := task.NewTaskWorker("full-system-worker", taskQueue, taskRepo, logger, workerConfig)
	taskManager := task.NewTaskManager(taskQueue, taskRepo, worker, logger)
	batchProcessor := batch.NewBatchProcessor(batchRepo, logger)

	// Register handlers
	emailHandler := &EmailJobHandler{logger: logger}
	notificationHandler := &NotificationTaskHandler{logger: logger}
	dataProcessor := &DataBatchProcessor{logger: logger}

	err := cronScheduler.RegisterHandler(emailHandler)
	require.NoError(t, err)

	err = worker.RegisterHandler(notificationHandler)
	require.NoError(t, err)

	err = batchProcessor.RegisterProcessor(dataProcessor)
	require.NoError(t, err)

	ctx := context.Background()

	// Start all components
	err = cronScheduler.Start(ctx)
	require.NoError(t, err)
	defer cronScheduler.Stop(ctx)

	err = taskManager.Start(ctx)
	require.NoError(t, err)
	defer taskManager.Stop(ctx)

	err = batchProcessor.Start(ctx)
	require.NoError(t, err)
	defer batchProcessor.Stop(ctx)

	// Create and submit various jobs/tasks

	// 1. Schedule job
	job := &schedulerEntity.Job{
		ID:         "full-system-job",
		Name:       "Full System Job",
		Type:       "email",
		Schedule:   time.Now().Add(time.Millisecond * 100),
		MaxRetries: 3,
	}

	err = cronScheduler.ScheduleJob(ctx, job)
	require.NoError(t, err)

	// 2. Submit task
	testTask := &taskEntity.Task{
		ID:         "full-system-task",
		Name:       "Full System Task",
		Type:       "notification",
		Priority:   taskEntity.TaskPriorityNormal,
		MaxRetries: 2,
	}

	err = taskManager.SubmitTask(ctx, testTask)
	require.NoError(t, err)

	// 3. Create batch job
	batchJob := &schedulerEntity.BatchJob{
		ID:         "full-system-batch",
		Name:       "Full System Batch",
		Type:       "data_processing",
		BatchSize:  5,
		TotalItems: 25,
		Status:     schedulerEntity.JobStatusPending,
		CreatedAt:  time.Now(),
		UpdatedAt:  time.Now(),
	}

	err = batchRepo.Save(ctx, batchJob)
	require.NoError(t, err)

	// Wait for all processing to complete with polling
	maxWait := time.Second * 10
	pollInterval := time.Millisecond * 100
	deadline := time.Now().Add(maxWait)

	for time.Now().Before(deadline) {
		if len(emailHandler.processed) >= 1 &&
			len(notificationHandler.processed) >= 1 &&
			len(dataProcessor.processed) >= 1 {
			break
		}
		time.Sleep(pollInterval)
	}

	// Verify all components processed their respective items
	assert.Contains(t, emailHandler.processed, job.ID)
	assert.Contains(t, notificationHandler.processed, testTask.ID)
	assert.Contains(t, dataProcessor.processed, batchJob.ID)

	// Verify final statuses with polling
	var retrievedJob *schedulerEntity.Job
	var retrievedTask *taskEntity.Task
	var retrievedBatch *schedulerEntity.BatchJob

	// First check if all items exist
	retrievedJob, jobErr := cronScheduler.GetJobStatus(ctx, job.ID)
	require.NoError(t, jobErr, "Should be able to get job from repository")
	require.NotNil(t, retrievedJob, "Job should exist in repository")

	retrievedTask, taskErr := taskManager.GetTaskStatus(ctx, testTask.ID)
	require.NoError(t, taskErr, "Should be able to get task from repository")
	require.NotNil(t, retrievedTask, "Task should exist in repository")

	retrievedBatch, batchErr := batchRepo.GetByID(ctx, batchJob.ID)
	require.NoError(t, batchErr, "Should be able to get batch job from repository")
	require.NotNil(t, retrievedBatch, "Batch job should exist in repository")

	// Poll for completion if not already completed
	deadline = time.Now().Add(maxWait)

	for time.Now().Before(deadline) {
		allCompleted := true

		if retrievedJob.Status != schedulerEntity.JobStatusCompleted {
			retrievedJob, jobErr = cronScheduler.GetJobStatus(ctx, job.ID)
			if jobErr != nil {
				allCompleted = false
			} else if retrievedJob == nil || retrievedJob.Status != schedulerEntity.JobStatusCompleted {
				allCompleted = false
			}
		}

		if retrievedTask.Status != taskEntity.TaskStatusCompleted {
			retrievedTask, taskErr = taskManager.GetTaskStatus(ctx, testTask.ID)
			if taskErr != nil {
				allCompleted = false
			} else if retrievedTask == nil || retrievedTask.Status != taskEntity.TaskStatusCompleted {
				allCompleted = false
			}
		}

		if retrievedBatch.Status != schedulerEntity.JobStatusCompleted {
			retrievedBatch, batchErr = batchRepo.GetByID(ctx, batchJob.ID)
			if batchErr != nil {
				allCompleted = false
			} else if retrievedBatch == nil || retrievedBatch.Status != schedulerEntity.JobStatusCompleted {
				allCompleted = false
			}
		}

		if allCompleted {
			break
		}
		time.Sleep(pollInterval)
	}

	require.NotNil(t, retrievedJob)
	require.NotNil(t, retrievedTask)
	require.NotNil(t, retrievedBatch)
	assert.Equal(t, schedulerEntity.JobStatusCompleted, retrievedJob.Status)
	assert.Equal(t, taskEntity.TaskStatusCompleted, retrievedTask.Status)
	assert.Equal(t, schedulerEntity.JobStatusCompleted, retrievedBatch.Status)
	assert.Equal(t, 25, retrievedBatch.ProcessedItems)
}
