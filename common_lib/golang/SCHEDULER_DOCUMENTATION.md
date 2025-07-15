# Scheduler & Task Processing Library

Common library cung cấp các pattern để xử lý scheduling, background tasks và batch processing với Go.

## Tính năng

### 1. Job Scheduler
- **Cron Jobs**: Hỗ trợ cron expressions cho việc lặp lại định kỳ
- **One-time Jobs**: Thực hiện job một lần tại thời điểm cụ thể
- **Retry Logic**: Tự động retry khi job thất bại
- **Concurrent Execution**: Chạy nhiều jobs đồng thời

### 2. Background Task Processing
- **Priority Queue**: Xử lý tasks theo độ ưu tiên
- **Distributed Processing**: Hỗ trợ nhiều workers
- **Retry Mechanism**: Tự động retry tasks thất bại
- **Delayed Tasks**: Thực hiện tasks sau một khoảng thời gian

### 3. Batch Processing
- **Chunked Processing**: Xử lý dữ liệu theo chunks
- **Progress Tracking**: Theo dõi tiến trình xử lý
- **Error Handling**: Xử lý lỗi trong batch processing
- **Scalable**: Có thể mở rộng cho large datasets

## Cài đặt

```bash
go get github.com/quantuanhuy/lib
```

## Dependencies

```go
require (
    github.com/robfig/cron/v3 v3.0.1
    github.com/go-redsync/redsync/v4 v4.13.0
    github.com/redis/go-redis/v9 v9.11.0
    go.uber.org/zap v1.27.0
    go.uber.org/fx v1.24.0
)
```

## Sử dụng

### 1. Khởi tạo Service

```go
package main

import (
    "context"
    "log"
    
    "github.com/quantuanhuy/lib/src/config"
    "github.com/quantuanhuy/lib/src/core/service"
    "github.com/quantuanhuy/lib/src/ui/bootstrap"
    "go.uber.org/fx"
    "go.uber.org/zap"
)

func main() {
    app := fx.New(
        bootstrap.SchedulerModule,
        fx.Invoke(func(schedulerService *service.SchedulerService) {
            ctx := context.Background()
            if err := schedulerService.Start(ctx); err != nil {
                log.Fatal(err)
            }
        }),
    )
    
    app.Run()
}
```

### 2. Tạo Job Handler

```go
package handlers

import (
    "context"
    "time"
    
    "github.com/quantuanhuy/lib/src/core/entity/scheduler"
    "go.uber.org/zap"
)

type EmailJobHandler struct {
    logger *zap.Logger
}

func NewEmailJobHandler(logger *zap.Logger) *EmailJobHandler {
    return &EmailJobHandler{logger: logger}
}

func (h *EmailJobHandler) Handle(ctx context.Context, job *scheduler.Job) error {
    to := job.Payload["to"].(string)
    subject := job.Payload["subject"].(string)
    body := job.Payload["body"].(string)
    
    // Implement email sending logic
    h.logger.Info("Sending email", 
        zap.String("to", to),
        zap.String("subject", subject))
    
    return nil
}

func (h *EmailJobHandler) GetType() string {
    return "email"
}
```

### 3. Schedule Jobs

```go
// One-time job
job := &scheduler.Job{
    ID:   "email-job-1",
    Name: "Welcome Email",
    Type: "email",
    Payload: map[string]interface{}{
        "to":      "user@example.com",
        "subject": "Welcome!",
        "body":    "Thank you for joining us.",
    },
    Schedule:   time.Now().Add(time.Minute * 5),
    MaxRetries: 3,
}

err := jobScheduler.ScheduleJob(ctx, job)

// Cron job
cronJob := &scheduler.Job{
    ID:       "daily-newsletter",
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

err = jobScheduler.ScheduleCronJob(ctx, cronJob)
```

### 4. Tạo Task Handler

```go
package handlers

import (
    "context"
    
    "github.com/quantuanhuy/lib/src/core/entity/task"
    "go.uber.org/zap"
)

type NotificationTaskHandler struct {
    logger *zap.Logger
}

func NewNotificationTaskHandler(logger *zap.Logger) *NotificationTaskHandler {
    return &NotificationTaskHandler{logger: logger}
}

func (h *NotificationTaskHandler) Handle(ctx context.Context, t *task.Task) error {
    userID := t.Payload["user_id"].(string)
    message := t.Payload["message"].(string)
    
    // Implement notification logic
    h.logger.Info("Sending notification",
        zap.String("user_id", userID),
        zap.String("message", message))
    
    return nil
}

func (h *NotificationTaskHandler) GetType() string {
    return "notification"
}
```

### 5. Submit Tasks

```go
// High priority task
task := &task.Task{
    ID:   "notification-1",
    Name: "User Notification",
    Type: "notification",
    Priority: task.TaskPriorityHigh,
    Payload: map[string]interface{}{
        "user_id": "user123",
        "message": "You have a new message!",
    },
    MaxRetries: 2,
}

err := taskManager.SubmitTask(ctx, task)

// Delayed task
delayedTask := &task.Task{
    ID:   "report-1",
    Name: "Monthly Report",
    Type: "report_generation",
    Priority: task.TaskPriorityNormal,
    Payload: map[string]interface{}{
        "type":       "monthly_sales",
        "date_range": "2024-01-01 to 2024-01-31",
    },
    MaxRetries: 1,
}

err = taskManager.SubmitDelayedTask(ctx, delayedTask, time.Hour*2)
```

### 6. Tạo Batch Processor

```go
package processors

import (
    "context"
    
    "github.com/quantuanhuy/lib/src/core/entity/scheduler"
    "go.uber.org/zap"
)

type DataProcessingBatchProcessor struct {
    logger *zap.Logger
}

func NewDataProcessingBatchProcessor(logger *zap.Logger) *DataProcessingBatchProcessor {
    return &DataProcessingBatchProcessor{logger: logger}
}

func (p *DataProcessingBatchProcessor) ProcessBatch(ctx context.Context, job *scheduler.BatchJob, items []interface{}) error {
    p.logger.Info("Processing batch",
        zap.String("job_id", job.ID),
        zap.Int("batch_size", len(items)))
    
    // Process each item in the batch
    for _, item := range items {
        // Implement your processing logic here
        p.logger.Debug("Processing item", zap.Any("item", item))
    }
    
    return nil
}

func (p *DataProcessingBatchProcessor) GetType() string {
    return "data_processing"
}
```

## Configuration

```go
package config

import (
    "time"
    
    "github.com/quantuanhuy/lib/src/config"
)

func CustomConfig() *config.SchedulerConfig {
    return &config.SchedulerConfig{
        Schedule: scheduler.ScheduleConfig{
            MaxConcurrentJobs: 20,
            RetryDelay:        time.Minute * 3,
            JobTimeout:        time.Hour * 2,
            CleanupInterval:   time.Hour * 12,
        },
        Worker: task.WorkerConfig{
            MaxConcurrentTasks: 10,
            RetryDelay:         time.Minute,
            TaskTimeout:        time.Minute * 15,
            PollInterval:       time.Second * 3,
        },
        Redis: config.RedisConfig{
            Host:     "localhost",
            Port:     6379,
            Password: "",
            DB:       0,
        },
    }
}
```

## Monitoring

### Job Status
```go
job, err := jobScheduler.GetJobStatus(ctx, "job-id")
if err != nil {
    log.Fatal(err)
}

fmt.Printf("Job Status: %s\n", job.Status)
```

### Task Status
```go
task, err := taskManager.GetTaskStatus(ctx, "task-id")
if err != nil {
    log.Fatal(err)
}

fmt.Printf("Task Status: %s\n", task.Status)
```

### Queue Status
```go
queueStatus, err := taskManager.GetQueueStatus(ctx)
if err != nil {
    log.Fatal(err)
}

fmt.Printf("Queue Size: %d\n", queueStatus.CurrentSize)
```

## Best Practices

1. **Error Handling**: Luôn implement proper error handling trong handlers
2. **Timeouts**: Đặt appropriate timeouts cho jobs và tasks
3. **Retry Logic**: Cấu hình retry strategies phù hợp
4. **Monitoring**: Implement logging và monitoring cho production
5. **Resource Management**: Giới hạn concurrent jobs/tasks để tránh resource exhaustion

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Job Scheduler │    │  Task Manager   │    │ Batch Processor │
│                 │    │                 │    │                 │
│ - Cron Jobs     │    │ - Priority Queue│    │ - Chunked Proc  │
│ - One-time Jobs │    │ - Workers       │    │ - Progress Track│
│ - Retry Logic   │    │ - Delayed Tasks │    │ - Error Handle  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   Redis Store   │
                    │                 │
                    │ - Job Storage   │
                    │ - Task Queue    │
                    │ - Batch Jobs    │
                    └─────────────────┘
```

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
