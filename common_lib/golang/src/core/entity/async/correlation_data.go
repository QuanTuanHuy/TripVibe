package entity

import "time"

// CorrelationData represents the correlation information stored for async requests
type CorrelationData struct {
	ID            string             `json:"id"`
	CorrelationID string             `json:"correlation_id"`
	RequestType   string             `json:"request_type"`
	Status        AsyncRequestStatus `json:"status"`
	ReplyTo       string             `json:"reply_to"`
	CreatedAt     time.Time          `json:"created_at"`
	UpdatedAt     time.Time          `json:"updated_at"`
	ExpiresAt     time.Time          `json:"expires_at"`
	Payload       map[string]interface{} `json:"payload,omitempty"`
}

// Business logic methods
func (cd *CorrelationData) IsExpired() bool {
	return time.Now().After(cd.ExpiresAt)
}

func (cd *CorrelationData) UpdateStatus(status AsyncRequestStatus) {
	cd.Status = status
	cd.UpdatedAt = time.Now()
}

func (cd *CorrelationData) ShouldCleanup() bool {
	return cd.IsExpired() || cd.Status == AsyncRequestStatusCompleted || 
		cd.Status == AsyncRequestStatusFailed || cd.Status == AsyncRequestStatusCancelled
}

// Factory method
func NewCorrelationData(correlationID, requestType, replyTo string, timeout time.Duration, payload map[string]interface{}) *CorrelationData {
	now := time.Now()
	return &CorrelationData{
		CorrelationID: correlationID,
		RequestType:   requestType,
		Status:        AsyncRequestStatusPending,
		ReplyTo:       replyTo,
		CreatedAt:     now,
		UpdatedAt:     now,
		ExpiresAt:     now.Add(timeout),
		Payload:       payload,
	}
}
