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

	MessageTypeNewMessage  = "message"
	MessageTypeTyping      = "typing"
	MessageTypeReadMessage = "read"
	MessageTypeError       = "error"

	MessageTypeUserConnected    = "user_connected"
	MessageTypeUserDisconnected = "user_disconnected"
)
