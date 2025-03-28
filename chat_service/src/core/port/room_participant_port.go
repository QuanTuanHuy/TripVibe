package port

import (
	"chat_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IRoomParticipantPort interface {
	CreateRoomParticipants(ctx context.Context, tx *gorm.DB, roomParticipants []*entity.RoomParticipantEntity) ([]*entity.RoomParticipantEntity, error)
	GetByRoomIDAndParticipantID(ctx context.Context, roomID, participantID int64) (*entity.RoomParticipantEntity, error)
	GetByRoomIDs(ctx context.Context, roomIDs []int64) ([]*entity.RoomParticipantEntity, error)
}
