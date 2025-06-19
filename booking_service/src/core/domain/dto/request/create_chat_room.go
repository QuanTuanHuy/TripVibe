package request

type CreateChatRoomDto struct {
	BookingID int64           `json:"bookingId"`
	Tourist   *ParticipantDto `json:"tourist"`
	Owner     *ParticipantDto `json:"owner"`
}

type ParticipantDto struct {
	UserID   int64  `json:"userId"`
	UserName string `json:"userName"`
	Role     string `json:"role"`
}
