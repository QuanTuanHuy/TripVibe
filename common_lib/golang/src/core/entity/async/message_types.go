package entity

import (
	"encoding/json"
	"time"
)

// MessageType represents the type of async message
type MessageType string

const (
	MessageTypeRequest   MessageType = "request"
	MessageTypeReply     MessageType = "reply"
	MessageTypeHeartbeat MessageType = "heartbeat"
	MessageTypeCancel    MessageType = "cancel"
)

// AsyncMessage represents a message in the async request-reply pattern
type AsyncMessage struct {
	ID            string                 `json:"id"`
	Type          MessageType            `json:"type"`
	CorrelationID string                 `json:"correlation_id"`
	RequestType   string                 `json:"request_type,omitempty"`
	ReplyTo       string                 `json:"reply_to,omitempty"`
	Payload       map[string]interface{} `json:"payload,omitempty"`
	Status        AsyncRequestStatus     `json:"status,omitempty"`
	Error         string                 `json:"error,omitempty"`
	Timestamp     time.Time              `json:"timestamp"`
	TTL           time.Duration          `json:"ttl,omitempty"`
}

// ToJSON converts AsyncMessage to JSON bytes
func (m *AsyncMessage) ToJSON() ([]byte, error) {
	return json.Marshal(m)
}

// FromJSON creates AsyncMessage from JSON bytes
func FromJSON(data []byte) (*AsyncMessage, error) {
	var msg AsyncMessage
	err := json.Unmarshal(data, &msg)
	return &msg, err
}
