package adapter

import (
	"chat_service/core/domain/entity"
	"chat_service/core/port"
	"chat_service/infrastructure/repository/mapper"
	"context"
	"gorm.io/gorm"
)

type RoomParticipantAdapter struct {
	base
}

func (r RoomParticipantAdapter) CreateRoomParticipants(ctx context.Context, tx *gorm.DB, roomParticipants []*entity.RoomParticipantEntity) ([]*entity.RoomParticipantEntity, error) {
	roomParticipantModels := mapper.ToRoomParticipantModels(roomParticipants)
	if err := tx.WithContext(ctx).Create(roomParticipantModels).Error; err != nil {
		return nil, err
	}
	return mapper.ToRoomParticipantEntities(roomParticipantModels), nil
}

func NewRoomParticipantAdapter(db *gorm.DB) port.IRoomParticipantPort {
	return &RoomParticipantAdapter{
		base: base{db: db},
	}
}
