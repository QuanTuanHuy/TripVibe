package examples

import (
	"context"
	"time"

	"github.com/quantuanhuy/lib/src/core/entity/scheduler"
	"github.com/quantuanhuy/lib/src/core/entity/task"
	"go.uber.org/zap"
)

// EmailJobHandler handles email sending jobs
type EmailJobHandler struct {
	logger *zap.Logger
}

func NewEmailJobHandler(logger *zap.Logger) *EmailJobHandler {
	return &EmailJobHandler{logger: logger}
}

func (h *EmailJobHandler) Handle(ctx context.Context, job *scheduler.Job) error {
	h.logger.Info("Processing email job",
		zap.String("job_id", job.ID),
		zap.Any("payload", job.Payload))

	// Simulate email sending
	to := job.Payload["to"].(string)
	subject := job.Payload["subject"].(string)
	_ = job.Payload["body"].(string) // body content for email

	h.logger.Info("Sending email",
		zap.String("to", to),
		zap.String("subject", subject))

	// Simulate processing time
	time.Sleep(time.Second * 2)

	h.logger.Info("Email sent successfully", zap.String("to", to))
	return nil
}

func (h *EmailJobHandler) GetType() string {
	return "email"
}

// NotificationTaskHandler handles notification tasks
type NotificationTaskHandler struct {
	logger *zap.Logger
}

func NewNotificationTaskHandler(logger *zap.Logger) *NotificationTaskHandler {
	return &NotificationTaskHandler{logger: logger}
}

func (h *NotificationTaskHandler) Handle(ctx context.Context, t *task.Task) error {
	h.logger.Info("Processing notification task",
		zap.String("task_id", t.ID),
		zap.Any("payload", t.Payload))

	// Simulate notification processing
	userID := t.Payload["user_id"].(string)
	message := t.Payload["message"].(string)

	h.logger.Info("Sending notification",
		zap.String("user_id", userID),
		zap.String("message", message))

	// Simulate processing time
	time.Sleep(time.Millisecond * 500)

	h.logger.Info("Notification sent successfully", zap.String("user_id", userID))
	return nil
}

func (h *NotificationTaskHandler) GetType() string {
	return "notification"
}

// DataProcessingBatchProcessor handles data processing in batches
type DataProcessingBatchProcessor struct {
	logger *zap.Logger
}

func NewDataProcessingBatchProcessor(logger *zap.Logger) *DataProcessingBatchProcessor {
	return &DataProcessingBatchProcessor{logger: logger}
}

func (p *DataProcessingBatchProcessor) ProcessBatch(ctx context.Context, job *scheduler.BatchJob, items []interface{}) error {
	p.logger.Info("Processing data batch",
		zap.String("job_id", job.ID),
		zap.Int("batch_size", len(items)))

	// Simulate batch processing
	for i, item := range items {
		p.logger.Debug("Processing item",
			zap.String("job_id", job.ID),
			zap.Int("item_index", i),
			zap.Any("item", item))

		// Simulate processing time for each item
		time.Sleep(time.Millisecond * 100)
	}

	p.logger.Info("Batch processed successfully",
		zap.String("job_id", job.ID),
		zap.Int("processed_items", len(items)))

	return nil
}

func (p *DataProcessingBatchProcessor) GetType() string {
	return "data_processing"
}

// ReportGenerationTaskHandler handles report generation tasks
type ReportGenerationTaskHandler struct {
	logger *zap.Logger
}

func NewReportGenerationTaskHandler(logger *zap.Logger) *ReportGenerationTaskHandler {
	return &ReportGenerationTaskHandler{logger: logger}
}

func (h *ReportGenerationTaskHandler) Handle(ctx context.Context, t *task.Task) error {
	h.logger.Info("Processing report generation task",
		zap.String("task_id", t.ID),
		zap.Any("payload", t.Payload))

	reportType := t.Payload["type"].(string)
	dateRange := t.Payload["date_range"].(string)

	h.logger.Info("Generating report",
		zap.String("type", reportType),
		zap.String("date_range", dateRange))

	// Simulate report generation (longer process)
	time.Sleep(time.Second * 10)

	h.logger.Info("Report generated successfully",
		zap.String("type", reportType),
		zap.String("task_id", t.ID))

	return nil
}

func (h *ReportGenerationTaskHandler) GetType() string {
	return "report_generation"
}
