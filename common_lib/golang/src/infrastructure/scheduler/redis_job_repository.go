package scheduler

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/quantuanhuy/lib/src/core/entity/scheduler"
	"github.com/quantuanhuy/lib/src/core/port"
	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"
)

type RedisJobRepository struct {
	client *redis.Client
	logger *zap.Logger
	prefix string
}

func NewRedisJobRepository(client *redis.Client, logger *zap.Logger) port.JobRepository {
	return &RedisJobRepository{
		client: client,
		logger: logger,
		prefix: "jobs:",
	}
}

func (r *RedisJobRepository) Save(ctx context.Context, job *scheduler.Job) error {
	key := r.prefix + job.ID
	data, err := json.Marshal(job)
	if err != nil {
		return fmt.Errorf("failed to marshal job: %w", err)
	}

	err = r.client.Set(ctx, key, data, 0).Err()
	if err != nil {
		return fmt.Errorf("failed to save job: %w", err)
	}

	// Add to status index
	statusKey := r.prefix + "status:" + string(job.Status)
	r.client.SAdd(ctx, statusKey, job.ID)

	// Add to type index
	typeKey := r.prefix + "type:" + job.Type
	r.client.SAdd(ctx, typeKey, job.ID)

	return nil
}

func (r *RedisJobRepository) GetByID(ctx context.Context, id string) (*scheduler.Job, error) {
	key := r.prefix + id
	data, err := r.client.Get(ctx, key).Result()
	if err != nil {
		if err == redis.Nil {
			return nil, fmt.Errorf("job not found: %s", id)
		}
		return nil, fmt.Errorf("failed to get job: %w", err)
	}

	var job scheduler.Job
	err = json.Unmarshal([]byte(data), &job)
	if err != nil {
		return nil, fmt.Errorf("failed to unmarshal job: %w", err)
	}

	return &job, nil
}

func (r *RedisJobRepository) GetByStatus(ctx context.Context, status scheduler.JobStatus) ([]*scheduler.Job, error) {
	statusKey := r.prefix + "status:" + string(status)
	jobIDs, err := r.client.SMembers(ctx, statusKey).Result()
	if err != nil {
		return nil, fmt.Errorf("failed to get job IDs by status: %w", err)
	}

	var jobs []*scheduler.Job
	for _, id := range jobIDs {
		job, err := r.GetByID(ctx, id)
		if err != nil {
			r.logger.Error("Failed to get job", zap.String("id", id), zap.Error(err))
			continue
		}
		jobs = append(jobs, job)
	}

	return jobs, nil
}

func (r *RedisJobRepository) GetPendingJobs(ctx context.Context, limit int) ([]*scheduler.Job, error) {
	jobs, err := r.GetByStatus(ctx, scheduler.JobStatusPending)
	if err != nil {
		return nil, err
	}

	if limit > 0 && len(jobs) > limit {
		jobs = jobs[:limit]
	}

	return jobs, nil
}

func (r *RedisJobRepository) Update(ctx context.Context, job *scheduler.Job) error {
	// Remove from old status index
	oldJob, err := r.GetByID(ctx, job.ID)
	if err == nil && oldJob.Status != job.Status {
		oldStatusKey := r.prefix + "status:" + string(oldJob.Status)
		r.client.SRem(ctx, oldStatusKey, job.ID)
	}

	return r.Save(ctx, job)
}

func (r *RedisJobRepository) Delete(ctx context.Context, id string) error {
	job, err := r.GetByID(ctx, id)
	if err != nil {
		return err
	}

	// Remove from indices
	statusKey := r.prefix + "status:" + string(job.Status)
	r.client.SRem(ctx, statusKey, id)

	typeKey := r.prefix + "type:" + job.Type
	r.client.SRem(ctx, typeKey, id)

	// Remove the job
	key := r.prefix + id
	return r.client.Del(ctx, key).Err()
}

func (r *RedisJobRepository) GetJobsByType(ctx context.Context, jobType string) ([]*scheduler.Job, error) {
	typeKey := r.prefix + "type:" + jobType
	jobIDs, err := r.client.SMembers(ctx, typeKey).Result()
	if err != nil {
		return nil, fmt.Errorf("failed to get job IDs by type: %w", err)
	}

	var jobs []*scheduler.Job
	for _, id := range jobIDs {
		job, err := r.GetByID(ctx, id)
		if err != nil {
			r.logger.Error("Failed to get job", zap.String("id", id), zap.Error(err))
			continue
		}
		jobs = append(jobs, job)
	}

	return jobs, nil
}

func (r *RedisJobRepository) GetJobsToRun(ctx context.Context, now time.Time) ([]*scheduler.Job, error) {
	pendingJobs, err := r.GetByStatus(ctx, scheduler.JobStatusPending)
	if err != nil {
		return nil, err
	}

	var jobsToRun []*scheduler.Job
	for _, job := range pendingJobs {
		if job.Schedule.Before(now) || job.Schedule.Equal(now) {
			jobsToRun = append(jobsToRun, job)
		}
	}

	return jobsToRun, nil
}

func (r *RedisJobRepository) CleanupCompletedJobs(ctx context.Context, olderThan time.Time) error {
	completedJobs, err := r.GetByStatus(ctx, scheduler.JobStatusCompleted)
	if err != nil {
		return err
	}

	for _, job := range completedJobs {
		if job.CompletedAt != nil && job.CompletedAt.Before(olderThan) {
			if err := r.Delete(ctx, job.ID); err != nil {
				r.logger.Error("Failed to delete completed job", zap.String("id", job.ID), zap.Error(err))
			}
		}
	}

	return nil
}
