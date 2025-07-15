package examples

import (
	"context"
	"time"

	"github.com/quantuanhuy/lib/src/core/entity/scheduler"
	"github.com/quantuanhuy/lib/src/core/entity/task"
	"github.com/quantuanhuy/lib/src/core/service"
	"github.com/quantuanhuy/lib/src/ui/bootstrap"
	"go.uber.org/fx"
	"go.uber.org/zap"
)

// ExampleUsage demonstrates how to use the scheduler service
func ExampleUsage() {
	app := fx.New(
		bootstrap.SchedulerModule,
		fx.Provide(
			NewExampleHandlers,
		),
		fx.Invoke(RunExample),
	)

	ctx := context.Background()
	if err := app.Start(ctx); err != nil {
		panic(err)
	}

	// Let it run for a while
	time.Sleep(time.Second * 30)

	if err := app.Stop(ctx); err != nil {
		panic(err)
	}
}

// ExampleHandlers holds all example handlers
type ExampleHandlers struct {
	EmailJobHandler              *EmailJobHandler
	NotificationTaskHandler      *NotificationTaskHandler
	DataProcessingBatchProcessor *DataProcessingBatchProcessor
	ReportGenerationTaskHandler  *ReportGenerationTaskHandler
}

// NewExampleHandlers creates example handlers
func NewExampleHandlers(logger *zap.Logger) *ExampleHandlers {
	return &ExampleHandlers{
		EmailJobHandler:              NewEmailJobHandler(logger),
		NotificationTaskHandler:      NewNotificationTaskHandler(logger),
		DataProcessingBatchProcessor: NewDataProcessingBatchProcessor(logger),
		ReportGenerationTaskHandler:  NewReportGenerationTaskHandler(logger),
	}
}

// RunExample demonstrates the scheduler functionality
func RunExample(schedulerService *service.SchedulerService, handlers *ExampleHandlers, logger *zap.Logger) {
	ctx := context.Background()

	// Start the scheduler service
	if err := schedulerService.Start(ctx); err != nil {
		logger.Fatal("Failed to start scheduler service", zap.Error(err))
	}

	// Register handlers
	jobScheduler := schedulerService.GetJobScheduler()
	taskManager := schedulerService.GetTaskManager()
	batchProcessor := schedulerService.GetBatchProcessor()

	// Register job handlers
	jobScheduler.RegisterHandler(handlers.EmailJobHandler)

	// Register task handlers - Note: In real implementation, you would need to access the worker
	// For now, we'll just demonstrate the concept
	logger.Info("Task handlers would be registered here")

	// Register batch processors
	batchProcessor.RegisterProcessor(handlers.DataProcessingBatchProcessor)

	// Schedule a job
	emailJob := &scheduler.Job{
		ID:   "email-job-1",
		Name: "Welcome Email",
		Type: "email",
		Payload: map[string]interface{}{
			"to":      "user@example.com",
			"subject": "Welcome to our platform!",
			"body":    "Thank you for joining us.",
		},
		Schedule:   time.Now().Add(time.Second * 5),
		MaxRetries: 3,
	}

	if err := jobScheduler.ScheduleJob(ctx, emailJob); err != nil {
		logger.Error("Failed to schedule job", zap.Error(err))
	}

	// Schedule a cron job
	cronJob := &scheduler.Job{
		ID:       "email-cron-job",
		Name:     "Daily Newsletter",
		Type:     "email",
		CronExpr: "0 9 * * *", // Every day at 9 AM
		Payload: map[string]interface{}{
			"to":      "subscribers@example.com",
			"subject": "Daily Newsletter",
			"body":    "Here's your daily newsletter.",
		},
		MaxRetries: 3,
	}

	if err := jobScheduler.ScheduleCronJob(ctx, cronJob); err != nil {
		logger.Error("Failed to schedule cron job", zap.Error(err))
	}

	// Submit a task
	notificationTask := &task.Task{
		ID:       "notification-task-1",
		Name:     "User Notification",
		Type:     "notification",
		Priority: task.TaskPriorityHigh,
		Payload: map[string]interface{}{
			"user_id": "user123",
			"message": "You have a new message!",
		},
		MaxRetries: 2,
	}

	if err := taskManager.SubmitTask(ctx, notificationTask); err != nil {
		logger.Error("Failed to submit task", zap.Error(err))
	}

	// Submit a delayed task
	reportTask := &task.Task{
		ID:       "report-task-1",
		Name:     "Monthly Report",
		Type:     "report_generation",
		Priority: task.TaskPriorityNormal,
		Payload: map[string]interface{}{
			"type":       "monthly_sales",
			"date_range": "2024-01-01 to 2024-01-31",
		},
		MaxRetries: 1,
	}

	if err := taskManager.SubmitDelayedTask(ctx, reportTask, time.Second*10); err != nil {
		logger.Error("Failed to submit delayed task", zap.Error(err))
	}

	// Create a batch job
	batchJob := &scheduler.BatchJob{
		ID:         "batch-job-1",
		Name:       "Data Processing Batch",
		Type:       "data_processing",
		BatchSize:  10,
		TotalItems: 100,
		Status:     scheduler.JobStatusPending,
		Payload: map[string]interface{}{
			"source": "user_data",
			"target": "processed_data",
		},
		CreatedAt: time.Now(),
		UpdatedAt: time.Now(),
	}

	// Note: You would need to implement batch job submission in the service
	// This is just for demonstration
	logger.Info("Example batch job created", zap.String("job_id", batchJob.ID))

	logger.Info("Example jobs and tasks scheduled successfully")
}
