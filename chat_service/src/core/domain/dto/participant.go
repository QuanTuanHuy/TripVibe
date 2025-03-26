package dto

type ParticipantDto struct {
	UserID   int64  `json:"userId"`
	UserName string `json:"userName"`
	Role     string `json:"role"`
}
