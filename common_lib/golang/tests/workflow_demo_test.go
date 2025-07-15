package test

import (
	"context"
	"testing"
	"time"

	"github.com/alicebob/miniredis/v2"
	schedulerEntity "github.com/quantuanhuy/lib/src/core/entity/scheduler"
	taskEntity "github.com/quantuanhuy/lib/src/core/entity/task"
	"github.com/quantuanhuy/lib/src/infrastructure/scheduler"
	"github.com/quantuanhuy/lib/src/infrastructure/task"
	"github.com/redis/go-redis/v9"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"go.uber.org/zap"
)

// Test để demo sự khác nhau giữa Task, Job, Batch
func TestTaskJobBatchDifferences(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	ctx := context.Background()

	t.Run("Task - Immediate Priority-Based Processing", func(t *testing.T) {
		// Tạo task queue
		taskQueue := task.NewRedisTaskQueue(client, logger, "demo-queue", 100)

		// Tạo 3 tasks với priority khác nhau
		highPriorityTask := &taskEntity.Task{
			ID:       "high-priority-task",
			Name:     "High Priority Task",
			Type:     "notification",
			Priority: taskEntity.TaskPriorityHigh, // 10
			Status:   taskEntity.TaskStatusPending,
			Payload: map[string]interface{}{
				"message": "Urgent notification",
			},
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		normalPriorityTask := &taskEntity.Task{
			ID:       "normal-priority-task",
			Name:     "Normal Priority Task",
			Type:     "email",
			Priority: taskEntity.TaskPriorityNormal, // 5
			Status:   taskEntity.TaskStatusPending,
			Payload: map[string]interface{}{
				"email": "user@example.com",
			},
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		lowPriorityTask := &taskEntity.Task{
			ID:       "low-priority-task",
			Name:     "Low Priority Task",
			Type:     "cleanup",
			Priority: taskEntity.TaskPriorityLow, // 1
			Status:   taskEntity.TaskStatusPending,
			Payload: map[string]interface{}{
				"cleanup_type": "temp_files",
			},
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		// Enqueue tasks (không theo thứ tự priority)
		err := taskQueue.Enqueue(ctx, lowPriorityTask)
		require.NoError(t, err)

		err = taskQueue.Enqueue(ctx, highPriorityTask)
		require.NoError(t, err)

		err = taskQueue.Enqueue(ctx, normalPriorityTask)
		require.NoError(t, err)

		// Dequeue với priority sẽ trả về task theo priority: HIGH -> NORMAL -> LOW
		// Cần cast để access DequeueWithPriority method
		redisQueue := taskQueue.(*task.RedisTaskQueue)

		firstTask, err := redisQueue.DequeueWithPriority(ctx)
		require.NoError(t, err)
		assert.Equal(t, "high-priority-task", firstTask.ID)
		assert.Equal(t, taskEntity.TaskPriorityHigh, firstTask.Priority)

		secondTask, err := redisQueue.DequeueWithPriority(ctx)
		require.NoError(t, err)
		assert.Equal(t, "normal-priority-task", secondTask.ID)
		assert.Equal(t, taskEntity.TaskPriorityNormal, secondTask.Priority)

		thirdTask, err := redisQueue.DequeueWithPriority(ctx)
		require.NoError(t, err)
		assert.Equal(t, "low-priority-task", thirdTask.ID)
		assert.Equal(t, taskEntity.TaskPriorityLow, thirdTask.Priority)

		t.Logf("✅ TASK: Xử lý theo priority - HIGH (%d) -> NORMAL (%d) -> LOW (%d)",
			taskEntity.TaskPriorityHigh, taskEntity.TaskPriorityNormal, taskEntity.TaskPriorityLow)
	})

	t.Run("Job - Scheduled Time-Based Processing", func(t *testing.T) {
		// Tạo job repository
		jobRepo := scheduler.NewRedisJobRepository(client, logger)

		// Tạo job để chạy sau 1 giờ
		futureJob := &schedulerEntity.Job{
			ID:       "future-job",
			Name:     "Future Job",
			Type:     "report",
			Schedule: time.Now().Add(time.Hour), // Chạy sau 1 giờ
			Status:   schedulerEntity.JobStatusPending,
			Payload: map[string]interface{}{
				"report_type": "daily",
			},
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		// Tạo job để chạy ngay
		immediateJob := &schedulerEntity.Job{
			ID:       "immediate-job",
			Name:     "Immediate Job",
			Type:     "notification",
			Schedule: time.Now().Add(-time.Second), // Chạy ngay
			Status:   schedulerEntity.JobStatusPending,
			Payload: map[string]interface{}{
				"message": "Immediate notification",
			},
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		// Tạo recurring job với cron expression
		recurringJob := &schedulerEntity.Job{
			ID:       "recurring-job",
			Name:     "Recurring Job",
			Type:     "backup",
			CronExpr: "0 0 * * *",                    // Chạy hàng ngày lúc 12:00 AM
			Schedule: time.Now().Add(time.Hour * 24), // Next run sau 24h
			Status:   schedulerEntity.JobStatusPending,
			Payload: map[string]interface{}{
				"backup_type": "database",
			},
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		// Lưu jobs
		err := jobRepo.Save(ctx, futureJob)
		require.NoError(t, err)

		err = jobRepo.Save(ctx, immediateJob)
		require.NoError(t, err)

		err = jobRepo.Save(ctx, recurringJob)
		require.NoError(t, err)

		// Test lấy jobs cần chạy ngay
		jobsToRun, err := jobRepo.GetJobsToRun(ctx, time.Now())
		require.NoError(t, err)

		// Chỉ immediate job sẽ được trả về
		assert.Len(t, jobsToRun, 1)
		assert.Equal(t, "immediate-job", jobsToRun[0].ID)

		t.Logf("✅ JOB: Scheduled processing - Future job chờ đến %v", futureJob.Schedule)
		t.Logf("✅ JOB: Immediate job sẵn sàng chạy ngay")
		t.Logf("✅ JOB: Recurring job với cron '%s' sẽ chạy hàng ngày", recurringJob.CronExpr)
	})
}

func TestTaskProcessingWorkflow(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	ctx := context.Background()

	t.Run("Task Complete Workflow", func(t *testing.T) {
		// Setup components
		taskRepo := task.NewRedisTaskRepository(client, logger)
		taskQueue := task.NewRedisTaskQueue(client, logger, "workflow-queue", 100)

		// 1. Tạo task
		emailTask := &taskEntity.Task{
			ID:       "email-workflow-task",
			Name:     "Email Workflow Task",
			Type:     "email",
			Priority: taskEntity.TaskPriorityHigh,
			Status:   taskEntity.TaskStatusPending,
			Payload: map[string]interface{}{
				"to":      "user@example.com",
				"subject": "Welcome Email",
				"body":    "Welcome to our service!",
			},
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		// 2. Lưu task vào repository
		err := taskRepo.Save(ctx, emailTask)
		require.NoError(t, err)
		t.Logf("✅ Step 1: Task saved to repository")

		// 3. Enqueue task vào priority queue
		err = taskQueue.Enqueue(ctx, emailTask)
		require.NoError(t, err)
		t.Logf("✅ Step 2: Task enqueued to priority queue")

		// 4. Worker dequeue task
		dequeuedTask, err := taskQueue.Dequeue(ctx)
		require.NoError(t, err)
		assert.Equal(t, emailTask.ID, dequeuedTask.ID)
		t.Logf("✅ Step 3: Task dequeued by worker")

		// 5. Worker cập nhật status thành RUNNING
		dequeuedTask.Status = taskEntity.TaskStatusRunning
		dequeuedTask.UpdatedAt = time.Now()
		err = taskRepo.Update(ctx, dequeuedTask)
		require.NoError(t, err)
		t.Logf("✅ Step 4: Task status updated to RUNNING")

		// 6. Worker xử lý task (simulate processing)
		time.Sleep(time.Millisecond * 100) // Simulate processing time

		// 7. Worker cập nhật status thành COMPLETED
		dequeuedTask.Status = taskEntity.TaskStatusCompleted
		dequeuedTask.UpdatedAt = time.Now()
		completedAt := time.Now()
		dequeuedTask.CompletedAt = &completedAt
		err = taskRepo.Update(ctx, dequeuedTask)
		require.NoError(t, err)
		t.Logf("✅ Step 5: Task processing completed")

		// 8. Verify final state
		finalTask, err := taskRepo.GetByID(ctx, emailTask.ID)
		require.NoError(t, err)
		assert.Equal(t, taskEntity.TaskStatusCompleted, finalTask.Status)
		assert.NotNil(t, finalTask.CompletedAt)
		t.Logf("✅ Step 6: Final verification - Task completed successfully")
	})
}

func TestJobSchedulingWorkflow(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	ctx := context.Background()

	t.Run("Job Scheduling Workflow", func(t *testing.T) {
		// Setup components
		jobRepo := scheduler.NewRedisJobRepository(client, logger)

		// 1. Tạo scheduled job
		reportJob := &schedulerEntity.Job{
			ID:       "daily-report-job",
			Name:     "Daily Report Job",
			Type:     "report",
			Schedule: time.Now().Add(time.Hour), // Chạy sau 1 giờ
			Status:   schedulerEntity.JobStatusPending,
			Payload: map[string]interface{}{
				"report_type": "daily_sales",
				"format":      "pdf",
			},
			MaxRetries: 3,
			CreatedAt:  time.Now(),
			UpdatedAt:  time.Now(),
		}

		// 2. Lưu job vào repository
		err := jobRepo.Save(ctx, reportJob)
		require.NoError(t, err)
		t.Logf("✅ Step 1: Job scheduled for %v", reportJob.Schedule)

		// 3. Scheduler check jobs cần chạy (simulate scheduler tick)
		currentTime := time.Now()
		jobsToRun, err := jobRepo.GetJobsToRun(ctx, currentTime)
		require.NoError(t, err)

		// Job chưa đến thời gian chạy
		assert.Len(t, jobsToRun, 0)
		t.Logf("✅ Step 2: No jobs to run at %v", currentTime)

		// 4. Simulate time passing (giả lập thời gian trôi qua)
		futureTime := time.Now().Add(time.Hour + time.Minute)
		jobsToRun, err = jobRepo.GetJobsToRun(ctx, futureTime)
		require.NoError(t, err)

		// Bây giờ job sẵn sàng chạy
		assert.Len(t, jobsToRun, 1)
		assert.Equal(t, reportJob.ID, jobsToRun[0].ID)
		t.Logf("✅ Step 3: Job ready to run at %v", futureTime)

		// 5. Scheduler thực thi job
		jobToExecute := jobsToRun[0]
		jobToExecute.Status = schedulerEntity.JobStatusRunning
		jobToExecute.UpdatedAt = time.Now()
		err = jobRepo.Update(ctx, jobToExecute)
		require.NoError(t, err)
		t.Logf("✅ Step 4: Job execution started")

		// 6. Job handler xử lý (simulate processing)
		time.Sleep(time.Millisecond * 200) // Simulate processing time

		// 7. Job completed
		jobToExecute.Status = schedulerEntity.JobStatusCompleted
		jobToExecute.UpdatedAt = time.Now()
		completedAt := time.Now()
		jobToExecute.CompletedAt = &completedAt
		err = jobRepo.Update(ctx, jobToExecute)
		require.NoError(t, err)
		t.Logf("✅ Step 5: Job completed successfully")

		// 8. Verify final state
		finalJob, err := jobRepo.GetByID(ctx, reportJob.ID)
		require.NoError(t, err)
		assert.Equal(t, schedulerEntity.JobStatusCompleted, finalJob.Status)
		assert.NotNil(t, finalJob.CompletedAt)
		t.Logf("✅ Step 6: Final verification - Job completed at %v", finalJob.CompletedAt)
	})
}

func TestPriorityProcessingDemo(t *testing.T) {
	mr := miniredis.RunT(t)
	defer mr.Close()

	client := redis.NewClient(&redis.Options{
		Addr: mr.Addr(),
	})

	logger := zap.NewNop()
	ctx := context.Background()

	t.Run("Priority Processing Order", func(t *testing.T) {
		taskQueue := task.NewRedisTaskQueue(client, logger, "priority-demo", 100)

		// Tạo nhiều tasks với priority khác nhau
		tasks := []*taskEntity.Task{
			{
				ID:       "task-1",
				Name:     "Low Priority Task 1",
				Type:     "cleanup",
				Priority: taskEntity.TaskPriorityLow, // 1
				Status:   taskEntity.TaskStatusPending,
			},
			{
				ID:       "task-2",
				Name:     "High Priority Task",
				Type:     "urgent",
				Priority: taskEntity.TaskPriorityHigh, // 10
				Status:   taskEntity.TaskStatusPending,
			},
			{
				ID:       "task-3",
				Name:     "Normal Priority Task",
				Type:     "normal",
				Priority: taskEntity.TaskPriorityNormal, // 5
				Status:   taskEntity.TaskStatusPending,
			},
			{
				ID:       "task-4",
				Name:     "Low Priority Task 2",
				Type:     "cleanup",
				Priority: taskEntity.TaskPriorityLow, // 1
				Status:   taskEntity.TaskStatusPending,
			},
			{
				ID:       "task-5",
				Name:     "High Priority Task 2",
				Type:     "urgent",
				Priority: taskEntity.TaskPriorityHigh, // 10
				Status:   taskEntity.TaskStatusPending,
			},
		}

		// Enqueue tất cả tasks (không theo thứ tự priority)
		for _, task := range tasks {
			err := taskQueue.Enqueue(ctx, task)
			require.NoError(t, err)
		}

		t.Logf("✅ Enqueued %d tasks with different priorities", len(tasks))

		// Dequeue và verify thứ tự priority (sử dụng DequeueWithPriority)
		// Không cần kiểm tra exact order vì tasks với cùng priority có thể khác thứ tự
		redisQueue := taskQueue.(*task.RedisTaskQueue)

		var dequeuedTasks []*taskEntity.Task
		for i := 0; i < 5; i++ {
			dequeuedTask, err := redisQueue.DequeueWithPriority(ctx)
			require.NoError(t, err)
			dequeuedTasks = append(dequeuedTasks, dequeuedTask)

			t.Logf("✅ Dequeued: %s (Priority: %d)", dequeuedTask.ID, dequeuedTask.Priority)
		}

		// Verify priority order: HIGH tasks first, then NORMAL, then LOW
		assert.Equal(t, taskEntity.TaskPriorityHigh, dequeuedTasks[0].Priority)
		assert.Equal(t, taskEntity.TaskPriorityHigh, dequeuedTasks[1].Priority)
		assert.Equal(t, taskEntity.TaskPriorityNormal, dequeuedTasks[2].Priority)
		assert.Equal(t, taskEntity.TaskPriorityLow, dequeuedTasks[3].Priority)
		assert.Equal(t, taskEntity.TaskPriorityLow, dequeuedTasks[4].Priority)

		// Queue should be empty
		size, err := taskQueue.Size(ctx)
		require.NoError(t, err)
		assert.Equal(t, 0, size)

		t.Logf("✅ All tasks processed in correct priority order")
	})
}
