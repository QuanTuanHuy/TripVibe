package scheduler

import (
	"context"
	"testing"
	"time"

	"github.com/alicebob/miniredis/v2"
	schedulerEntity "github.com/quantuanhuy/lib/src/core/entity/scheduler"
	"github.com/redis/go-redis/v9"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"go.uber.org/zap"
)

func TestRedisJobRepository_Save(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	repo := NewRedisJobRepository(client, logger)

	ctx := context.Background()
	testJob := &schedulerEntity.Job{
		ID:         "test-job-1",
		Name:       "Test Job",
		Type:       "test",
		Status:     schedulerEntity.JobStatusPending,
		Schedule:   time.Now(),
		MaxRetries: 3,
		CreatedAt:  time.Now(),
		UpdatedAt:  time.Now(),
		Payload: map[string]interface{}{
			"test_key": "test_value",
		},
	}

	err := repo.Save(ctx, testJob)
	require.NoError(t, err)

	// Verify job was saved
	retrievedJob, err := repo.GetByID(ctx, testJob.ID)
	require.NoError(t, err)
	assert.Equal(t, testJob.ID, retrievedJob.ID)
	assert.Equal(t, testJob.Name, retrievedJob.Name)
	assert.Equal(t, testJob.Type, retrievedJob.Type)
	assert.Equal(t, testJob.Status, retrievedJob.Status)
	assert.Equal(t, testJob.MaxRetries, retrievedJob.MaxRetries)
}

func TestRedisJobRepository_GetByStatus(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	repo := NewRedisJobRepository(client, logger)

	ctx := context.Background()

	// Create jobs with different statuses
	jobs := []*schedulerEntity.Job{
		{
			ID:         "job-1",
			Name:       "Job 1",
			Type:       "test",
			Status:     schedulerEntity.JobStatusPending,
			Schedule:   time.Now(),
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		},
		{
			ID:         "job-2",
			Name:       "Job 2",
			Type:       "test",
			Status:     schedulerEntity.JobStatusRunning,
			Schedule:   time.Now(),
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		},
		{
			ID:         "job-3",
			Name:       "Job 3",
			Type:       "test",
			Status:     schedulerEntity.JobStatusCompleted,
			Schedule:   time.Now(),
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		},
	}

	for _, job := range jobs {
		err := repo.Save(ctx, job)
		require.NoError(t, err)
	}

	// Test get pending jobs
	pendingJobs, err := repo.GetByStatus(ctx, schedulerEntity.JobStatusPending)
	require.NoError(t, err)
	assert.Len(t, pendingJobs, 1)
	assert.Equal(t, "job-1", pendingJobs[0].ID)

	// Test get running jobs
	runningJobs, err := repo.GetByStatus(ctx, schedulerEntity.JobStatusRunning)
	require.NoError(t, err)
	assert.Len(t, runningJobs, 1)
	assert.Equal(t, "job-2", runningJobs[0].ID)

	// Test get completed jobs
	completedJobs, err := repo.GetByStatus(ctx, schedulerEntity.JobStatusCompleted)
	require.NoError(t, err)
	assert.Len(t, completedJobs, 1)
	assert.Equal(t, "job-3", completedJobs[0].ID)
}

func TestRedisJobRepository_GetJobsToRun(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	repo := NewRedisJobRepository(client, logger)

	ctx := context.Background()
	now := time.Now()

	// Create jobs with different schedules
	jobs := []*schedulerEntity.Job{
		{
			ID:         "job-past",
			Name:       "Past Job",
			Type:       "test",
			Status:     schedulerEntity.JobStatusPending,
			Schedule:   now.Add(-1 * time.Hour), // Past
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		},
		{
			ID:         "job-now",
			Name:       "Now Job",
			Type:       "test",
			Status:     schedulerEntity.JobStatusPending,
			Schedule:   now, // Now
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		},
		{
			ID:         "job-future",
			Name:       "Future Job",
			Type:       "test",
			Status:     schedulerEntity.JobStatusPending,
			Schedule:   now.Add(1 * time.Hour), // Future
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		},
	}

	for _, job := range jobs {
		err := repo.Save(ctx, job)
		require.NoError(t, err)
	}

	// Get jobs to run
	jobsToRun, err := repo.GetJobsToRun(ctx, now)
	require.NoError(t, err)
	assert.Len(t, jobsToRun, 2) // past and now jobs

	// Verify correct jobs are returned
	jobIDs := make([]string, len(jobsToRun))
	for i, job := range jobsToRun {
		jobIDs[i] = job.ID
	}
	assert.Contains(t, jobIDs, "job-past")
	assert.Contains(t, jobIDs, "job-now")
	assert.NotContains(t, jobIDs, "job-future")
}

func TestRedisJobRepository_Update(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	repo := NewRedisJobRepository(client, logger)

	ctx := context.Background()

	testJob := &schedulerEntity.Job{
		ID:         "test-job-1",
		Name:       "Test Job",
		Type:       "test",
		Status:     schedulerEntity.JobStatusPending,
		Schedule:   time.Now(),
		MaxRetries: 3,
		CreatedAt:  time.Now(),
		UpdatedAt:  time.Now(),
	}

	err := repo.Save(ctx, testJob)
	require.NoError(t, err)

	// Update job status
	testJob.Status = schedulerEntity.JobStatusRunning
	testJob.UpdatedAt = time.Now()

	err = repo.Update(ctx, testJob)
	require.NoError(t, err)

	// Verify update
	retrievedJob, err := repo.GetByID(ctx, testJob.ID)
	require.NoError(t, err)
	assert.Equal(t, schedulerEntity.JobStatusRunning, retrievedJob.Status)

	// Verify indices are updated
	runningJobs, err := repo.GetByStatus(ctx, schedulerEntity.JobStatusRunning)
	require.NoError(t, err)
	assert.Len(t, runningJobs, 1)

	pendingJobs, err := repo.GetByStatus(ctx, schedulerEntity.JobStatusPending)
	require.NoError(t, err)
	assert.Len(t, pendingJobs, 0)
}

func TestRedisJobRepository_CleanupCompletedJobs(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	repo := NewRedisJobRepository(client, logger)

	ctx := context.Background()

	// Create old completed job
	oldTime := time.Now().Add(-2 * time.Hour)
	oldJob := &schedulerEntity.Job{
		ID:          "old-job",
		Name:        "Old Job",
		Type:        "test",
		Status:      schedulerEntity.JobStatusCompleted,
		Schedule:    oldTime,
		MaxRetries:  3,
		CreatedAt:   oldTime,
		UpdatedAt:   oldTime,
		CompletedAt: &oldTime,
	}

	// Create recent completed job
	recentTime := time.Now().Add(-30 * time.Minute)
	recentJob := &schedulerEntity.Job{
		ID:          "recent-job",
		Name:        "Recent Job",
		Type:        "test",
		Status:      schedulerEntity.JobStatusCompleted,
		Schedule:    recentTime,
		MaxRetries:  3,
		CreatedAt:   recentTime,
		UpdatedAt:   recentTime,
		CompletedAt: &recentTime,
	}

	err := repo.Save(ctx, oldJob)
	require.NoError(t, err)

	err = repo.Save(ctx, recentJob)
	require.NoError(t, err)

	// Cleanup jobs older than 1 hour
	cutoffTime := time.Now().Add(-1 * time.Hour)
	err = repo.CleanupCompletedJobs(ctx, cutoffTime)
	require.NoError(t, err)

	// Verify old job is deleted
	_, err = repo.GetByID(ctx, oldJob.ID)
	assert.Error(t, err)

	// Verify recent job still exists
	_, err = repo.GetByID(ctx, recentJob.ID)
	assert.NoError(t, err)
}

// Mock job handler for testing
type MockJobHandler struct {
	processed []string
	shouldErr bool
}

func (h *MockJobHandler) Handle(ctx context.Context, job *schedulerEntity.Job) error {
	h.processed = append(h.processed, job.ID)
	if h.shouldErr {
		return assert.AnError
	}
	return nil
}

func (h *MockJobHandler) GetType() string {
	return "mock"
}

func TestCronJobScheduler_RegisterHandler(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	repo := NewRedisJobRepository(client, logger)
	config := &schedulerEntity.ScheduleConfig{
		MaxConcurrentJobs: 10,
		RetryDelay:        time.Minute,
		JobTimeout:        time.Hour,
		CleanupInterval:   time.Hour,
	}

	scheduler := NewCronJobScheduler(repo, logger, config)

	handler := &MockJobHandler{}
	err := scheduler.RegisterHandler(handler)
	require.NoError(t, err)
}

func TestCronJobScheduler_ScheduleJob(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	repo := NewRedisJobRepository(client, logger)
	config := &schedulerEntity.ScheduleConfig{
		MaxConcurrentJobs: 10,
		RetryDelay:        time.Minute,
		JobTimeout:        time.Hour,
		CleanupInterval:   time.Hour,
	}

	scheduler := NewCronJobScheduler(repo, logger, config)

	ctx := context.Background()
	testJob := &schedulerEntity.Job{
		ID:         "test-job-1",
		Name:       "Test Job",
		Type:       "test",
		Schedule:   time.Now().Add(time.Hour),
		MaxRetries: 3,
	}

	err := scheduler.ScheduleJob(ctx, testJob)
	require.NoError(t, err)

	// Verify job was saved
	retrievedJob, err := scheduler.GetJobStatus(ctx, testJob.ID)
	require.NoError(t, err)
	assert.Equal(t, testJob.ID, retrievedJob.ID)
	assert.Equal(t, schedulerEntity.JobStatusPending, retrievedJob.Status)
}

func TestCronJobScheduler_CancelJob(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	repo := NewRedisJobRepository(client, logger)
	config := &schedulerEntity.ScheduleConfig{
		MaxConcurrentJobs: 10,
		RetryDelay:        time.Minute,
		JobTimeout:        time.Hour,
		CleanupInterval:   time.Hour,
	}

	scheduler := NewCronJobScheduler(repo, logger, config)

	ctx := context.Background()
	testJob := &schedulerEntity.Job{
		ID:         "test-job-1",
		Name:       "Test Job",
		Type:       "test",
		Schedule:   time.Now().Add(time.Hour),
		MaxRetries: 3,
	}

	err := scheduler.ScheduleJob(ctx, testJob)
	require.NoError(t, err)

	// Cancel job
	err = scheduler.CancelJob(ctx, testJob.ID)
	require.NoError(t, err)

	// Verify job is cancelled
	retrievedJob, err := scheduler.GetJobStatus(ctx, testJob.ID)
	require.NoError(t, err)
	assert.Equal(t, schedulerEntity.JobStatusCancelled, retrievedJob.Status)
}
