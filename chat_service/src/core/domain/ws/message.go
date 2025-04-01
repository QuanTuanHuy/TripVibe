package ws

type WebSocketMessage struct {
	Type    string      `json:"type"`
	Payload interface{} `json:"payload"`
}

// Message types
const (
	MessageTypeNewMessage    = "new_message"
	MessageTypeReadMessage   = "read_message"
	MessageTypeUserConnected = "user_connected"
)
