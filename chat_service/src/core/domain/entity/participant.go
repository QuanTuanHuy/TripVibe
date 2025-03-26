package entity

import "chat_service/core/domain/constant"

type ParticipantEntity struct {
	UserID   int64             `json:"userId"`
	UserName string            `json:"userName"`
	Role     constant.UserRole `json:"role" bson:"role"`
}
