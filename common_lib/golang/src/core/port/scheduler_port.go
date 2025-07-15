package port

import (
	"context"
	"time"

	"github.com/quantuanhuy/lib/src/core/entity/scheduler"
)

// JobRepository defines the interface for job persistence
type JobRepository interface {
	Save(ctx context.Context, job *scheduler.Job) error
	GetByID(ctx context.Context, id string) (*scheduler.Job, error)
	GetByStatus(ctx context.Context, status scheduler.JobStatus) ([]*scheduler.Job, error)
	GetPendingJobs(ctx context.Context, limit int) ([]*scheduler.Job, error)
	Update(ctx context.Context, job *scheduler.Job) error
	Delete(ctx context.Context, id string) error
	GetJobsByType(ctx context.Context, jobType string) ([]*scheduler.Job, error)
	GetJobsToRun(ctx context.Context, now time.Time) ([]*scheduler.Job, error)
	CleanupCompletedJobs(ctx context.Context, olderThan time.Time) error
}

// JobScheduler defines the interface for job scheduling
type JobScheduler interface {
	ScheduleJob(ctx context.Context, job *scheduler.Job) error
	ScheduleCronJob(ctx context.Context, job *scheduler.Job) error
	CancelJob(ctx context.Context, jobID string) error
	Start(ctx context.Context) error
	Stop(ctx context.Context) error
	RegisterHandler(handler scheduler.JobHandler) error
	GetJobStatus(ctx context.Context, jobID string) (*scheduler.Job, error)
	GetRunningJobs(ctx context.Context) ([]*scheduler.Job, error)
}

// BatchJobRepository defines the interface for batch job persistence
type BatchJobRepository interface {
	Save(ctx context.Context, job *scheduler.BatchJob) error
	GetByID(ctx context.Context, id string) (*scheduler.BatchJob, error)
	GetByStatus(ctx context.Context, status scheduler.JobStatus) ([]*scheduler.BatchJob, error)
	Update(ctx context.Context, job *scheduler.BatchJob) error
	Delete(ctx context.Context, id string) error
}

// BatchProcessor defines the interface for batch processing
type BatchProcessor interface {
	ProcessBatch(ctx context.Context, job *scheduler.BatchJob, processor scheduler.BatchProcessor) error
	RegisterProcessor(processor scheduler.BatchProcessor) error
	Start(ctx context.Context) error
	Stop(ctx context.Context) error
}
