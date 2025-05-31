package constant

type MessageType string

const (
	TextMessage   MessageType = "text"
	MediaMessage  MessageType = "media"
	SystemMessage MessageType = "system"
)
