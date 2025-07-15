package entity

import (
	"context"
	"time"
)

// TaskStatus represents the status of a background task
type TaskStatus string

const (
	TaskStatusPending   TaskStatus = "pending"
	TaskStatusRunning   TaskStatus = "running"
	TaskStatusCompleted TaskStatus = "completed"
	TaskStatusFailed    TaskStatus = "failed"
	TaskStatusCancelled TaskStatus = "cancelled"
)

// TaskPriority represents the priority of a background task
type TaskPriority int

const (
	TaskPriorityLow      TaskPriority = 1
	TaskPriorityNormal   TaskPriority = 5
	TaskPriorityHigh     TaskPriority = 10
	TaskPriorityCritical TaskPriority = 15
)

// Task represents a background task
type Task struct {
	ID          string                 `json:"id"`
	Name        string                 `json:"name"`
	Type        string                 `json:"type"`
	Priority    TaskPriority           `json:"priority"`
	Status      TaskStatus             `json:"status"`
	Payload     map[string]interface{} `json:"payload,omitempty"`
	Retries     int                    `json:"retries"`
	MaxRetries  int                    `json:"max_retries"`
	Delay       time.Duration          `json:"delay"`
	CreatedAt   time.Time              `json:"created_at"`
	UpdatedAt   time.Time              `json:"updated_at"`
	StartedAt   *time.Time             `json:"started_at,omitempty"`
	CompletedAt *time.Time             `json:"completed_at,omitempty"`
	Error       string                 `json:"error,omitempty"`
}

// TaskHandler defines the interface for task handlers
type TaskHandler interface {
	Handle(ctx context.Context, task *Task) error
	GetType() string
}

// TaskResult represents the result of a task execution
type TaskResult struct {
	TaskID    string        `json:"task_id"`
	Success   bool          `json:"success"`
	Error     string        `json:"error,omitempty"`
	Output    string        `json:"output,omitempty"`
	Duration  time.Duration `json:"duration"`
	StartTime time.Time     `json:"start_time"`
	EndTime   time.Time     `json:"end_time"`
}
