package entity

import "time"

type AsyncRequestStatus string

const (
	AsyncRequestStatusPending    AsyncRequestStatus = "pending"
	AsyncRequestStatusProcessing AsyncRequestStatus = "processing"
	AsyncRequestStatusCompleted  AsyncRequestStatus = "completed"
	AsyncRequestStatusFailed     AsyncRequestStatus = "failed"
	AsyncRequestStatusTimeout    AsyncRequestStatus = "timeout"
	AsyncRequestStatusCancelled  AsyncRequestStatus = "cancelled"
)

// AsyncRequest represents an asynchronous request - Pure entity with domain logic only
type AsyncRequest struct {
	ID            string                 `json:"id"`
	CorrelationID string                 `json:"correlation_id"`
	RequestType   string                 `json:"request_type"`
	Status        AsyncRequestStatus     `json:"status"`
	Payload       map[string]interface{} `json:"payload,omitempty"`
	Timeout       time.Duration          `json:"timeout,omitempty"`
	CreatedAt     time.Time              `json:"created_at"`
	UpdatedAt     time.Time              `json:"updated_at"`
	ProcessedAt   *time.Time             `json:"processed_at,omitempty"`
	CompletedAt   *time.Time             `json:"completed_at,omitempty"`
	Error         string                 `json:"error,omitempty"`
}

// Business logic methods
func (r *AsyncRequest) IsExpired() bool {
	if r.Timeout <= 0 {
		return false
	}
	return time.Since(r.CreatedAt) > r.Timeout
}

func (r *AsyncRequest) MarkAsProcessing() {
	r.Status = AsyncRequestStatusProcessing
	now := time.Now()
	r.ProcessedAt = &now
	r.UpdatedAt = now
}

func (r *AsyncRequest) MarkAsCompleted() {
	r.Status = AsyncRequestStatusCompleted
	now := time.Now()
	r.CompletedAt = &now
	r.UpdatedAt = now
}

func (r *AsyncRequest) MarkAsFailed(errorMsg string) {
	r.Status = AsyncRequestStatusFailed
	r.Error = errorMsg
	now := time.Now()
	r.CompletedAt = &now
	r.UpdatedAt = now
}

func (r *AsyncRequest) MarkAsTimeout() {
	r.Status = AsyncRequestStatusTimeout
	now := time.Now()
	r.CompletedAt = &now
	r.UpdatedAt = now
}

func (r *AsyncRequest) IsComplete() bool {
	return r.Status == AsyncRequestStatusCompleted ||
		r.Status == AsyncRequestStatusFailed ||
		r.Status == AsyncRequestStatusTimeout ||
		r.Status == AsyncRequestStatusCancelled
}
