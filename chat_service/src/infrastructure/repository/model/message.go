package model

import "time"

type MessageModel struct {
	BaseModel
	ChatRoomID  int64      `gorm:"column:chat_room_id"`
	SenderID    *int64     `gorm:"column:sender_id"`
	Content     string     `gorm:"column:content"`
	Type        string     `gorm:"column:type"`
	IsRead      bool       `gorm:"column:is_read"`      // Deprecated: Use Status field instead
	Status      string     `gorm:"column:status"`       // New: Message delivery status
	DeliveredAt *time.Time `gorm:"column:delivered_at"` // New: Timestamp when message was delivered
	ReadAt      *time.Time `gorm:"column:read_at"`      // New: Timestamp when message was read
}

func (MessageModel) TableName() string {
	return "messages"
}
