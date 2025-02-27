package model

type NotificationModel struct {
	BaseModel
	Type       string `gorm:"column:type"`
	Content    string `gorm:"column:content"`
	ReceiverID int64  `gorm:"column:receiver_id"`
	IsRead     bool   `gorm:"column:is_read"`
	Status     string `gorm:"column:status"`
}

func (n *NotificationModel) TableName() string {
	return "notifications"
}
