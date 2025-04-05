package entity

// NotificationEntity represents a notification in the domain
type NotificationEntity struct {
	ID          int64                  `json:"id"`
	Type        string                 `json:"type" validate:"required"`
	Title       string                 `json:"title" validate:"required"`
	Content     string                 `json:"content" validate:"required"`
	UserID      int64                  `json:"userId" validate:"required"`
	Recipient   string                 `json:"recipient" validate:"required"`
	Metadata    map[string]interface{} `json:"metadata"`
	Status      string                 `json:"status"`
	RetryCount  int                    `json:"retryCount"`
	SentAt      *int64                 `json:"sentAt"`
	LastRetryAt *int64                 `json:"lastRetryAt"`
	CreatedAt   int64                  `json:"createdAt"`
	UpdatedAt   int64                  `json:"updatedAt"`
}
