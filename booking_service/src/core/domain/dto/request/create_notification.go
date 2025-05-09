package request

type CreateNotificationDto struct {
	UserID    int64  `json:"userId"`
	Type      string `json:"type"`
	Title     string `json:"title"`
	Content   string `json:"content"`
	Recipient string `json:"recipient"`
}
