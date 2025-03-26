package port

import (
	"chat_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IRoomParticipantPort interface {
	CreateRoomParticipants(ctx context.Context, tx *gorm.DB, roomParticipants []*entity.RoomParticipantEntity) ([]*entity.RoomParticipantEntity, error)
}
