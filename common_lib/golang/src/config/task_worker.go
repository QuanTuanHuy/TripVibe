package config

import "time"

// WorkerConfig represents the configuration for task workers
type WorkerConfig struct {
	MaxConcurrentTasks int           `json:"max_concurrent_tasks"`
	RetryDelay         time.Duration `json:"retry_delay"`
	TaskTimeout        time.Duration `json:"task_timeout"`
	PollInterval       time.Duration `json:"poll_interval"`
}
