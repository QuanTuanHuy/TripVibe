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

// AsyncRequest represents an asynchronous request
type AsyncRequest struct {
	ID            string                 `json:"id"`
	CorrelationID string                 `json:"correlation_id"`
	RequestType   string                 `json:"request_type"`
	Status        AsyncRequestStatus     `json:"status"`
	Payload       map[string]interface{} `json:"payload,omitempty"`
	ReplyTo       string                 `json:"reply_to,omitempty"`
	Timeout       time.Duration          `json:"timeout,omitempty"`
	CreatedAt     time.Time              `json:"created_at"`
	UpdatedAt     time.Time              `json:"updated_at"`
	ProcessedAt   *time.Time             `json:"processed_at,omitempty"`
	CompletedAt   *time.Time             `json:"completed_at,omitempty"`
	Error         string                 `json:"error,omitempty"`
}
