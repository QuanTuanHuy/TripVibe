package entity

type ChatRoomEntity struct {
	BaseEntity
	BookingID     int64                `json:"bookingId"`
	LastMessageID int64                `json:"lastMessageId"`
	Participants  []*ParticipantEntity `json:"participants"`
	LastMessage   *MessageEntity       `json:"lastMessage,omitempty"`
}
