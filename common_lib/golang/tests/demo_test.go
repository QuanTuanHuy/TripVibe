package test

import (
	"context"
	"fmt"
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

// Demo handlers for testing
type EmailHandler struct {
	SentEmails []string
	Logger     *zap.Logger
}

func (h *EmailHandler) Handle(ctx context.Context, job *schedulerEntity.Job) error {
	h.Logger.Info("Processing email job", zap.String("job_id", job.ID))

	// Simulate email sending
	if payload, ok := job.Payload["email"]; ok {
		email := payload.(string)
		h.SentEmails = append(h.SentEmails, email)
		h.Logger.Info("Email sent", zap.String("email", email))
	}

	return nil
}

func (h *EmailHandler) GetType() string {
	return "email"
}

type NotificationHandler struct {
	SentNotifications []string
	Logger            *zap.Logger
}

func (h *NotificationHandler) Handle(ctx context.Context, task *taskEntity.Task) error {
	h.Logger.Info("Processing notification task", zap.String("task_id", task.ID))

	// Simulate notification sending
	if payload, ok := task.Payload["message"]; ok {
		message := payload.(string)
		h.SentNotifications = append(h.SentNotifications, message)
		h.Logger.Info("Notification sent", zap.String("message", message))
	}

	return nil
}

func (h *NotificationHandler) GetType() string {
	return "notification"
}

func TestSchedulerDemo(t *testing.T) {
	// Setup Redis
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	ctx := context.Background()

	// Create scheduler components
	jobRepo := scheduler.NewRedisJobRepository(client, logger)
	schedulerConfig := config.DefaultSchedulerConfig()

	cronScheduler := scheduler.NewCronJobScheduler(jobRepo, logger, &schedulerConfig.Schedule)

	// Create and register email handler
	emailHandler := &EmailHandler{
		SentEmails: make([]string, 0),
		Logger:     logger,
	}

	err := cronScheduler.RegisterHandler(emailHandler)
	require.NoError(t, err)

	// Start scheduler
	err = cronScheduler.Start(ctx)
	require.NoError(t, err)
	defer cronScheduler.Stop(ctx)

	// Schedule some jobs
	jobs := []*schedulerEntity.Job{
		{
			ID:       "email-job-1",
			Name:     "Welcome Email",
			Type:     "email",
			Schedule: time.Now().Add(time.Millisecond * 100),
			Status:   schedulerEntity.JobStatusPending,
			Payload: map[string]interface{}{
				"email": "user1@example.com",
			},
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		},
		{
			ID:       "email-job-2",
			Name:     "Newsletter",
			Type:     "email",
			Schedule: time.Now().Add(time.Millisecond * 200),
			Status:   schedulerEntity.JobStatusPending,
			Payload: map[string]interface{}{
				"email": "user2@example.com",
			},
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		},
	}

	for _, job := range jobs {
		err = cronScheduler.ScheduleJob(ctx, job)
		require.NoError(t, err)
	}

	// Wait for jobs to be processed with polling
	maxWait := time.Second * 5
	pollInterval := time.Millisecond * 100
	deadline := time.Now().Add(maxWait)

	for time.Now().Before(deadline) {
		if len(emailHandler.SentEmails) >= 2 {
			break
		}
		time.Sleep(pollInterval)
	}

	// Verify emails were sent
	assert.Contains(t, emailHandler.SentEmails, "user1@example.com")
	assert.Contains(t, emailHandler.SentEmails, "user2@example.com")

	fmt.Printf("Demo: Sent %d emails\n", len(emailHandler.SentEmails))
}

func TestTaskManagerDemo(t *testing.T) {
	// Setup Redis
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	ctx := context.Background()

	// Create task components
	taskRepo := task.NewRedisTaskRepository(client, logger)
	taskQueue := task.NewRedisTaskQueue(client, logger, "demo-queue", 100)

	config := config.DefaultSchedulerConfig()
	worker := task.NewTaskWorker("demo-worker", taskQueue, taskRepo, logger, &config.Worker)
	taskManager := task.NewTaskManager(taskQueue, taskRepo, worker, logger)

	// Create and register notification handler
	notificationHandler := &NotificationHandler{
		SentNotifications: make([]string, 0),
		Logger:            logger,
	}

	err := worker.RegisterHandler(notificationHandler)
	require.NoError(t, err)

	// Start task manager
	err = taskManager.Start(ctx)
	require.NoError(t, err)
	defer taskManager.Stop(ctx)

	// Submit some tasks
	tasks := []*taskEntity.Task{
		{
			ID:       "notification-task-1",
			Name:     "Push Notification",
			Type:     "notification",
			Priority: taskEntity.TaskPriorityHigh,
			Status:   taskEntity.TaskStatusPending,
			Payload: map[string]interface{}{
				"message": "You have a new message!",
			},
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		},
		{
			ID:       "notification-task-2",
			Name:     "System Alert",
			Type:     "notification",
			Priority: taskEntity.TaskPriorityNormal,
			Status:   taskEntity.TaskStatusPending,
			Payload: map[string]interface{}{
				"message": "System maintenance scheduled",
			},
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		},
		{
			ID:       "notification-task-3",
			Name:     "Low Priority Info",
			Type:     "notification",
			Priority: taskEntity.TaskPriorityLow,
			Status:   taskEntity.TaskStatusPending,
			Payload: map[string]interface{}{
				"message": "Information update available",
			},
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		},
	}

	for _, task := range tasks {
		err = taskManager.SubmitTask(ctx, task)
		require.NoError(t, err)
	}

	// Wait for tasks to be processed with polling
	maxWait := time.Second * 5
	pollInterval := time.Millisecond * 100
	deadline := time.Now().Add(maxWait)

	for time.Now().Before(deadline) {
		if len(notificationHandler.SentNotifications) >= 3 {
			break
		}
		time.Sleep(pollInterval)
	}

	// Verify notifications were sent
	assert.Contains(t, notificationHandler.SentNotifications, "You have a new message!")
	assert.Contains(t, notificationHandler.SentNotifications, "System maintenance scheduled")
	assert.Contains(t, notificationHandler.SentNotifications, "Information update available")

	fmt.Printf("Demo: Sent %d notifications\n", len(notificationHandler.SentNotifications))
}

func TestFullWorkflowDemo(t *testing.T) {
	// Setup Redis
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	ctx := context.Background()

	// Create all components
	jobRepo := scheduler.NewRedisJobRepository(client, logger)
	taskRepo := task.NewRedisTaskRepository(client, logger)
	taskQueue := task.NewRedisTaskQueue(client, logger, "workflow-queue", 100)

	config := config.DefaultSchedulerConfig()
	cronScheduler := scheduler.NewCronJobScheduler(jobRepo, logger, &config.Schedule)
	worker := task.NewTaskWorker("workflow-worker", taskQueue, taskRepo, logger, &config.Worker)
	taskManager := task.NewTaskManager(taskQueue, taskRepo, worker, logger)

	// Create handlers
	emailHandler := &EmailHandler{
		SentEmails: make([]string, 0),
		Logger:     logger,
	}
	notificationHandler := &NotificationHandler{
		SentNotifications: make([]string, 0),
		Logger:            logger,
	}

	// Register handlers
	err := cronScheduler.RegisterHandler(emailHandler)
	require.NoError(t, err)
	err = worker.RegisterHandler(notificationHandler)
	require.NoError(t, err)

	// Start all components
	err = cronScheduler.Start(ctx)
	require.NoError(t, err)
	defer cronScheduler.Stop(ctx)

	err = taskManager.Start(ctx)
	require.NoError(t, err)
	defer taskManager.Stop(ctx)

	// Schedule a job
	emailJob := &schedulerEntity.Job{
		ID:       "workflow-email-job",
		Name:     "Daily Report Email",
		Type:     "email",
		Schedule: time.Now().Add(time.Millisecond * 100),
		Status:   schedulerEntity.JobStatusPending,
		Payload: map[string]interface{}{
			"email": "admin@example.com",
		},
		MaxRetries: 3,
		CreatedAt:  time.Now(),
		UpdatedAt:  time.Now(),
	}

	err = cronScheduler.ScheduleJob(ctx, emailJob)
	require.NoError(t, err)

	// Submit a task
	notificationTask := &taskEntity.Task{
		ID:       "workflow-notification-task",
		Name:     "Urgent Alert",
		Type:     "notification",
		Priority: taskEntity.TaskPriorityHigh,
		Status:   taskEntity.TaskStatusPending,
		Payload: map[string]interface{}{
			"message": "Critical system alert!",
		},
		MaxRetries: 3,
		CreatedAt:  time.Now(),
		UpdatedAt:  time.Now(),
	}

	err = taskManager.SubmitTask(ctx, notificationTask)
	require.NoError(t, err)

	// Wait for processing with polling
	maxWait := time.Second * 5
	pollInterval := time.Millisecond * 100
	deadline := time.Now().Add(maxWait)

	for time.Now().Before(deadline) {
		if len(emailHandler.SentEmails) >= 1 && len(notificationHandler.SentNotifications) >= 1 {
			break
		}
		time.Sleep(pollInterval)
	}

	// Verify both were processed
	assert.Contains(t, emailHandler.SentEmails, "admin@example.com")
	assert.Contains(t, notificationHandler.SentNotifications, "Critical system alert!")

	fmt.Printf("Demo: Full workflow completed - %d emails, %d notifications\n",
		len(emailHandler.SentEmails), len(notificationHandler.SentNotifications))
}
