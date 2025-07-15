package scheduler

import (
	"context"
	"time"
)

// JobStatus represents the status of a job
type JobStatus string

const (
	JobStatusPending   JobStatus = "pending"
	JobStatusRunning   JobStatus = "running"
	JobStatusCompleted JobStatus = "completed"
	JobStatusFailed    JobStatus = "failed"
	JobStatusCancelled JobStatus = "cancelled"
)

// Job represents a scheduled job
type Job struct {
	ID          string                 `json:"id"`
	Name        string                 `json:"name"`
	Type        string                 `json:"type"`
	CronExpr    string                 `json:"cron_expr,omitempty"`
	Schedule    time.Time              `json:"schedule"`
	Status      JobStatus              `json:"status"`
	Payload     map[string]interface{} `json:"payload,omitempty"`
	Retries     int                    `json:"retries"`
	MaxRetries  int                    `json:"max_retries"`
	CreatedAt   time.Time              `json:"created_at"`
	UpdatedAt   time.Time              `json:"updated_at"`
	CompletedAt *time.Time             `json:"completed_at,omitempty"`
	Error       string                 `json:"error,omitempty"`
}

// JobHandler defines the interface for job handlers
type JobHandler interface {
	Handle(ctx context.Context, job *Job) error
	GetType() string
}

// JobResult represents the result of a job execution
type JobResult struct {
	JobID     string        `json:"job_id"`
	Success   bool          `json:"success"`
	Error     string        `json:"error,omitempty"`
	Output    string        `json:"output,omitempty"`
	Duration  time.Duration `json:"duration"`
	StartTime time.Time     `json:"start_time"`
	EndTime   time.Time     `json:"end_time"`
}

// ScheduleConfig represents the configuration for scheduling jobs
type ScheduleConfig struct {
	MaxConcurrentJobs int           `json:"max_concurrent_jobs"`
	RetryDelay        time.Duration `json:"retry_delay"`
	JobTimeout        time.Duration `json:"job_timeout"`
	CleanupInterval   time.Duration `json:"cleanup_interval"`
}

// BatchJob represents a batch processing job
type BatchJob struct {
	ID             string                 `json:"id"`
	Name           string                 `json:"name"`
	Type           string                 `json:"type"`
	BatchSize      int                    `json:"batch_size"`
	TotalItems     int                    `json:"total_items"`
	ProcessedItems int                    `json:"processed_items"`
	Status         JobStatus              `json:"status"`
	Payload        map[string]interface{} `json:"payload,omitempty"`
	CreatedAt      time.Time              `json:"created_at"`
	UpdatedAt      time.Time              `json:"updated_at"`
	CompletedAt    *time.Time             `json:"completed_at,omitempty"`
	Error          string                 `json:"error,omitempty"`
}

// BatchProcessor defines the interface for batch processing
type BatchProcessor interface {
	ProcessBatch(ctx context.Context, job *BatchJob, items []interface{}) error
	GetType() string
}
