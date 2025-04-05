package entity

type RoomParticipantEntity struct {
	BaseEntity
	ChatRoomID    int64 `json:"chatRoomId"`
	ParticipantID int64 `json:"participantId"`
}
