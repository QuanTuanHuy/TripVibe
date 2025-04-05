package request

type CreateMessageRequest struct {
	SenderID    int64  `json:"senderId"`
	Content     string `json:"content"`
	MessageType string `json:"messageType"`
}
