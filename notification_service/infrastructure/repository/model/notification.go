package model

import "time"

type NotificationModel struct {
	BaseModel
	UserID      int64      `gorm:"column:user_id"`
	Type        string     `gorm:"column:type"`
	Title       string     `gorm:"column:title"`
	Content     string     `gorm:"column:content"`
	Recipient   string     `gorm:"column:recipient"`
	Status      string     `gorm:"column:status"`
	Metadata    string     `gorm:"column:metadata"`
	RetryCount  int        `gorm:"column:retry_count"`
	LastRetryAt *time.Time `gorm:"column:last_retry_at"`
	SentAt      *time.Time `gorm:"column:sent_at"`
}

func (n *NotificationModel) TableName() string {
	return "notifications"
}
