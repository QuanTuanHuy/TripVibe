package model

type ChatRoomModel struct {
	BaseModel
	BookingID     int64 `gorm:"column:booking_id"`
	LastMessageID int64 `gorm:"column:last_message_id"`
}

func (ChatRoomModel) TableName() string {
	return "chat_rooms"
}
