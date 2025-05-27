package ws

import "chat_service/core/domain/constant"

type WebSocketMessage struct {
	Type    string      `json:"type"`
	Payload interface{} `json:"payload"`
}

// Message types
const (
	MessageTypeNewMessage     = "new_message"
	MessageTypeReadMessage    = "read_message"
	MessageTypeUserConnected  = "user_connected"
	MessageTypeStatusUpdate   = "message_status_update"
	MessageTypeAcknowledgment = "acknowledgment"
)

// StatusUpdatePayload represents the payload for message status updates
type StatusUpdatePayload struct {
	MessageID int64                   `json:"messageId"`
	OldStatus constant.MessageStatus `json:"oldStatus"`
	NewStatus constant.MessageStatus `json:"newStatus"`
	UpdatedBy int64                   `json:"updatedBy"`
	UpdatedAt int64                   `json:"updatedAt"`
	RoomID    int64                   `json:"roomId"`
}

// AcknowledgmentPayload represents the payload for message acknowledgments
type AcknowledgmentPayload struct {
	MessageType string `json:"messageType"`
	MessageID   *int64 `json:"messageId,omitempty"`
	Status      string `json:"status"` // "success" or "error"
	Error       string `json:"error,omitempty"`
}
