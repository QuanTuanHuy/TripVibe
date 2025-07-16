package entity

import (
	"time"
)

// AsyncRequestStatus represents the status of an async request
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
	Payload       map[string]interface{} `json:"payload"`
	ReplyTo       string                 `json:"reply_to"`
	Timeout       time.Duration          `json:"timeout"`
	CreatedAt     time.Time              `json:"created_at"`
	UpdatedAt     time.Time              `json:"updated_at"`
	ProcessedAt   *time.Time             `json:"processed_at,omitempty"`
	CompletedAt   *time.Time             `json:"completed_at,omitempty"`
	Error         string                 `json:"error,omitempty"`
}

// AsyncReply represents an asynchronous reply
type AsyncReply struct {
	ID            string                 `json:"id"`
	CorrelationID string                 `json:"correlation_id"`
	RequestID     string                 `json:"request_id"`
	Status        AsyncRequestStatus     `json:"status"`
	Result        map[string]interface{} `json:"result,omitempty"`
	Error         string                 `json:"error,omitempty"`
	ProcessedAt   time.Time              `json:"processed_at"`
	Duration      time.Duration          `json:"duration"`
}

// AsyncCallback represents a callback function for async operations
type AsyncCallback func(reply *AsyncReply)

// AsyncRequestHandler represents a handler for async requests
type AsyncRequestHandler func(request *AsyncRequest) (*AsyncReply, error)
