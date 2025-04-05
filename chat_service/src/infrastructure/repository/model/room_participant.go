package model

type RoomParticipantModel struct {
	BaseModel
	ChatRoomID    int64 `gorm:"column:chat_room_id"`
	ParticipantID int64 `gorm:"column:participant_id"`
}

func (RoomParticipantModel) TableName() string {
	return "room_participants"
}
