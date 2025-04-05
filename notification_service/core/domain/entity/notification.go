package entity

type NotificationType string

const (
	EmailNotification NotificationType = "EMAIL"
	SMSNotification   NotificationType = "SMS"
	PushNotification  NotificationType = "PUSH"
)

type NotificationStatus string

const (
	StatusPending    NotificationStatus = "PENDING"
	StatusSent       NotificationStatus = "SENT"
	StatusFailed     NotificationStatus = "FAILED"
	StatusProcessing NotificationStatus = "PROCESSING"
	StatusCancelled  NotificationStatus = "CANCELLED"
)

type NotificationEntity struct {
	BaseEntity
	UserID      int64                  `json:"userId"`
	Type        NotificationType       `json:"type"`
	Title       string                 `json:"title"`
	Content     string                 `json:"content"`
	Recipient   string                 `json:"recipient"`
	Status      NotificationStatus     `json:"status"`
	Metadata    map[string]interface{} `json:"metadata"`
	RetryCount  int                    `json:"retryCount"`
	LastRetryAt *int64                 `json:"lastRetryAt"`
	SentAt      *int64                 `json:"sentAt"`
}
