package model

type MessageModel struct {
	BaseModel
	ChatRoomID  int64  `gorm:"column:chat_room_id"`
	SenderID    *int64 `gorm:"column:sender_id"`
	Content     string `gorm:"column:content"`
	Type        string `gorm:"column:type"`
	IsRead      bool   `gorm:"column:is_read"`
	MediaDataID *int64 `gorm:"column:media_data_id"`
}

func (MessageModel) TableName() string {
	return "messages"
}
