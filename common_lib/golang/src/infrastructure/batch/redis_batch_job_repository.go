package batch

import (
	"context"
	"encoding/json"
	"fmt"

	"github.com/quantuanhuy/lib/src/core/entity/scheduler"
	"github.com/quantuanhuy/lib/src/core/port"
	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"
)

type RedisBatchJobRepository struct {
	client *redis.Client
	logger *zap.Logger
	prefix string
}

func NewRedisBatchJobRepository(client *redis.Client, logger *zap.Logger) port.BatchJobRepository {
	return &RedisBatchJobRepository{
		client: client,
		logger: logger,
		prefix: "batch_jobs:",
	}
}

func (r *RedisBatchJobRepository) Save(ctx context.Context, job *scheduler.BatchJob) error {
	key := r.prefix + job.ID
	data, err := json.Marshal(job)
	if err != nil {
		return fmt.Errorf("failed to marshal batch job: %w", err)
	}

	err = r.client.Set(ctx, key, data, 0).Err()
	if err != nil {
		return fmt.Errorf("failed to save batch job: %w", err)
	}

	// Add to status index
	statusKey := r.prefix + "status:" + string(job.Status)
	r.client.SAdd(ctx, statusKey, job.ID)

	return nil
}

func (r *RedisBatchJobRepository) GetByID(ctx context.Context, id string) (*scheduler.BatchJob, error) {
	key := r.prefix + id
	data, err := r.client.Get(ctx, key).Result()
	if err != nil {
		if err == redis.Nil {
			return nil, fmt.Errorf("batch job not found: %s", id)
		}
		return nil, fmt.Errorf("failed to get batch job: %w", err)
	}

	var job scheduler.BatchJob
	err = json.Unmarshal([]byte(data), &job)
	if err != nil {
		return nil, fmt.Errorf("failed to unmarshal batch job: %w", err)
	}

	return &job, nil
}

func (r *RedisBatchJobRepository) GetByStatus(ctx context.Context, status scheduler.JobStatus) ([]*scheduler.BatchJob, error) {
	statusKey := r.prefix + "status:" + string(status)
	jobIDs, err := r.client.SMembers(ctx, statusKey).Result()
	if err != nil {
		return nil, fmt.Errorf("failed to get batch job IDs by status: %w", err)
	}

	var jobs []*scheduler.BatchJob
	for _, id := range jobIDs {
		job, err := r.GetByID(ctx, id)
		if err != nil {
			r.logger.Error("Failed to get batch job", zap.String("id", id), zap.Error(err))
			continue
		}
		jobs = append(jobs, job)
	}

	return jobs, nil
}

func (r *RedisBatchJobRepository) Update(ctx context.Context, job *scheduler.BatchJob) error {
	// Remove from old status index
	oldJob, err := r.GetByID(ctx, job.ID)
	if err == nil && oldJob.Status != job.Status {
		oldStatusKey := r.prefix + "status:" + string(oldJob.Status)
		r.client.SRem(ctx, oldStatusKey, job.ID)
	}

	return r.Save(ctx, job)
}

func (r *RedisBatchJobRepository) Delete(ctx context.Context, id string) error {
	job, err := r.GetByID(ctx, id)
	if err != nil {
		return err
	}

	// Remove from status index
	statusKey := r.prefix + "status:" + string(job.Status)
	r.client.SRem(ctx, statusKey, id)

	// Remove the job
	key := r.prefix + id
	return r.client.Del(ctx, key).Err()
}
