package config

import (
	"time"

	"github.com/quantuanhuy/lib/src/core/entity/scheduler"
	"github.com/quantuanhuy/lib/src/core/entity/task"
)

// SchedulerConfig represents the configuration for the scheduler
type SchedulerConfig struct {
	Schedule scheduler.ScheduleConfig `mapstructure:"schedule"`
	Worker   task.WorkerConfig        `mapstructure:"worker"`
	Redis    RedisConfig              `mapstructure:"redis"`
}

// RedisConfig represents Redis connection configuration
type RedisConfig struct {
	Host     string `mapstructure:"host"`
	Port     int    `mapstructure:"port"`
	Password string `mapstructure:"password"`
	DB       int    `mapstructure:"db"`
}

// DefaultSchedulerConfig returns default scheduler configuration
func DefaultSchedulerConfig() *SchedulerConfig {
	return &SchedulerConfig{
		Schedule: scheduler.ScheduleConfig{
			MaxConcurrentJobs: 10,
			RetryDelay:        time.Minute * 5,
			JobTimeout:        time.Hour,
			CleanupInterval:   time.Hour * 24,
		},
		Worker: task.WorkerConfig{
			MaxConcurrentTasks: 5,
			RetryDelay:         time.Minute * 2,
			TaskTimeout:        time.Minute * 30,
			PollInterval:       time.Second * 5,
		},
		Redis: RedisConfig{
			Host:     "localhost",
			Port:     6379,
			Password: "",
			DB:       0,
		},
	}
}

// BatchConfig represents the configuration for batch processing
type BatchConfig struct {
	MaxConcurrentJobs int           `mapstructure:"max_concurrent_jobs"`
	DefaultBatchSize  int           `mapstructure:"default_batch_size"`
	ProcessTimeout    time.Duration `mapstructure:"process_timeout"`
	CheckInterval     time.Duration `mapstructure:"check_interval"`
}

// DefaultBatchConfig returns default batch configuration
func DefaultBatchConfig() *BatchConfig {
	return &BatchConfig{
		MaxConcurrentJobs: 5,
		DefaultBatchSize:  100,
		ProcessTimeout:    time.Hour,
		CheckInterval:     time.Second * 10,
	}
}

// TaskQueueConfig represents the configuration for task queues
type TaskQueueConfig struct {
	MaxQueueSize int    `mapstructure:"max_queue_size"`
	QueueName    string `mapstructure:"queue_name"`
}

// DefaultTaskQueueConfig returns default task queue configuration
func DefaultTaskQueueConfig() *TaskQueueConfig {
	return &TaskQueueConfig{
		MaxQueueSize: 10000,
		QueueName:    "default_task_queue",
	}
}
