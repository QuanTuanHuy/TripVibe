package entity

import "time"

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
