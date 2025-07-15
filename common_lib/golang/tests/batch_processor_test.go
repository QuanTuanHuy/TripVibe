package test

import (
	"context"
	"fmt"
	"testing"
	"time"

	"github.com/alicebob/miniredis/v2"
	schedulerEntity "github.com/quantuanhuy/lib/src/core/entity/scheduler"
	"github.com/quantuanhuy/lib/src/infrastructure/batch"
	"github.com/redis/go-redis/v9"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"go.uber.org/zap"
)

type MockBatchProcessor struct {
	processedJobs []string
	shouldFail    bool
	logger        *zap.Logger
}

func (p *MockBatchProcessor) ProcessBatch(ctx context.Context, job *schedulerEntity.BatchJob, items []interface{}) error {
	if p.shouldFail {
		return assert.AnError
	}

	p.processedJobs = append(p.processedJobs, job.ID)
	p.logger.Info("Mock batch processed", zap.String("job_id", job.ID))
	return nil
}

func (p *MockBatchProcessor) GetType() string {
	return "mock_batch"
}

func TestRedisBatchJobRepository(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	repo := batch.NewRedisBatchJobRepository(client, logger)
	ctx := context.Background()

	t.Run("Save and Get Batch Job", func(t *testing.T) {
		batchJob := &schedulerEntity.BatchJob{
			ID:         "test-batch-save",
			Name:       "Test Batch Save",
			Type:       "test_batch",
			BatchSize:  20,
			TotalItems: 200,
			Status:     schedulerEntity.JobStatusPending,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		err := repo.Save(ctx, batchJob)
		require.NoError(t, err)

		retrievedJob, err := repo.GetByID(ctx, batchJob.ID)
		require.NoError(t, err)
		assert.Equal(t, batchJob.ID, retrievedJob.ID)
		assert.Equal(t, batchJob.Name, retrievedJob.Name)
		assert.Equal(t, batchJob.Type, retrievedJob.Type)
		assert.Equal(t, batchJob.BatchSize, retrievedJob.BatchSize)
		assert.Equal(t, batchJob.TotalItems, retrievedJob.TotalItems)
		assert.Equal(t, batchJob.Status, retrievedJob.Status)
	})

	t.Run("Update Batch Job", func(t *testing.T) {
		batchJob := &schedulerEntity.BatchJob{
			ID:         "test-batch-update",
			Name:       "Test Batch Update",
			Type:       "test_batch",
			BatchSize:  15,
			TotalItems: 150,
			Status:     schedulerEntity.JobStatusPending,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		err := repo.Save(ctx, batchJob)
		require.NoError(t, err)

		// Update job
		batchJob.Status = schedulerEntity.JobStatusRunning
		batchJob.ProcessedItems = 50
		batchJob.UpdatedAt = time.Now()

		err = repo.Save(ctx, batchJob)
		require.NoError(t, err)

		retrievedJob, err := repo.GetByID(ctx, batchJob.ID)
		require.NoError(t, err)
		assert.Equal(t, schedulerEntity.JobStatusRunning, retrievedJob.Status)
		assert.Equal(t, 50, retrievedJob.ProcessedItems)
	})

	t.Run("Get Jobs By Status", func(t *testing.T) {
		// Create fresh Redis instance for this test
		mr2 := miniredis.RunT(t)
		defer mr2.Close()

		client2 := redis.NewClient(&redis.Options{
			Addr: mr2.Addr(),
		})

		repo2 := batch.NewRedisBatchJobRepository(client2, logger)

		// Create pending jobs
		for i := 0; i < 3; i++ {
			batchJob := &schedulerEntity.BatchJob{
				ID:         fmt.Sprintf("pending-batch-status-%d", i),
				Name:       fmt.Sprintf("Pending Batch Status %d", i),
				Type:       "test_batch_status",
				BatchSize:  10,
				TotalItems: 100,
				Status:     schedulerEntity.JobStatusPending,
				CreatedAt:  time.Now(),
				UpdatedAt:  time.Now(),
			}

			err := repo2.Save(ctx, batchJob)
			require.NoError(t, err)
		}

		// Create completed job
		completedJob := &schedulerEntity.BatchJob{
			ID:         "completed-batch-status",
			Name:       "Completed Batch Status",
			Type:       "test_batch_status",
			BatchSize:  10,
			TotalItems: 100,
			Status:     schedulerEntity.JobStatusCompleted,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		err := repo2.Save(ctx, completedJob)
		require.NoError(t, err)

		// Get pending jobs
		pendingJobs, err := repo2.GetByStatus(ctx, schedulerEntity.JobStatusPending)
		require.NoError(t, err)
		assert.Len(t, pendingJobs, 3)

		for _, job := range pendingJobs {
			assert.Equal(t, schedulerEntity.JobStatusPending, job.Status)
		}

		// Get completed jobs
		completedJobs, err := repo2.GetByStatus(ctx, schedulerEntity.JobStatusCompleted)
		require.NoError(t, err)
		assert.Len(t, completedJobs, 1)
		assert.Equal(t, "completed-batch-status", completedJobs[0].ID)
	})

	t.Run("Delete Batch Job", func(t *testing.T) {
		batchJob := &schedulerEntity.BatchJob{
			ID:         "test-batch-delete",
			Name:       "Test Batch Delete",
			Type:       "test_batch",
			BatchSize:  10,
			TotalItems: 100,
			Status:     schedulerEntity.JobStatusPending,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		err := repo.Save(ctx, batchJob)
		require.NoError(t, err)

		err = repo.Delete(ctx, batchJob.ID)
		require.NoError(t, err)

		_, err = repo.GetByID(ctx, batchJob.ID)
		assert.Error(t, err)
	})

	t.Run("Get Non-existent Batch Job", func(t *testing.T) {
		_, err := repo.GetByID(ctx, "non-existent-batch")
		assert.Error(t, err)
	})
}
