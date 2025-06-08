package ws

type WebSocketMessage struct {
	Type    string      `json:"type"`
	Payload interface{} `json:"data"`
	RoomID  *int64      `json:"roomId,omitempty"`
}

// Message types
const (
	MessageTypeAuth        = "auth"
	MessageTypeAuthSuccess = "auth_success"
	MessageTypeAuthError   = "auth_error"

	MessageTypeNewMessage  = "new_message"
	MessageTypeTyping      = "typing_indicator"
	MessageTypeReadMessage = "message_read"
	MessageTypeError       = "error"

	MessageTypeUserConnected    = "user_connected"
	MessageTypeUserDisconnected = "user_disconnected"
)
