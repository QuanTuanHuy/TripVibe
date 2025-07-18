package entity

import (
	"time"
)

// AsyncReply represents an asynchronous reply - Pure entity
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

// Business logic methods
func (r *AsyncReply) IsSuccess() bool {
	return r.Status == AsyncRequestStatusCompleted && r.Error == ""
}

func (r *AsyncReply) IsFailure() bool {
	return r.Status == AsyncRequestStatusFailed || r.Error != ""
}

func (r *AsyncReply) IsTimeout() bool {
	return r.Status == AsyncRequestStatusTimeout
}

// Value objects for callbacks - moved from entity to prevent coupling
type AsyncCallback func(reply *AsyncReply)

// Handler interface for request processing
type AsyncRequestHandler interface {
	Handle(request *AsyncRequest) (*AsyncReply, error)
}
