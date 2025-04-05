package constant

type MessageType string

const (
	TextMessage   MessageType = "text"
	ImageMessage  MessageType = "image"
	SystemMessage MessageType = "system"
)
