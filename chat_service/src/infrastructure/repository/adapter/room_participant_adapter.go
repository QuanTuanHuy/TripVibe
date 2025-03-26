package adapter

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/entity"
	"chat_service/core/port"
	"chat_service/infrastructure/repository/mapper"
	"chat_service/infrastructure/repository/model"
	"context"
	"errors"
	"gorm.io/gorm"
)

type RoomParticipantAdapter struct {
	base
}

func (r RoomParticipantAdapter) GetByRoomIDAndParticipantID(ctx context.Context, roomID, participantID int64) (*entity.RoomParticipantEntity, error) {
	var roomParticipant model.RoomParticipantModel
	if err := r.db.WithContext(ctx).Where("chat_room_id = ? AND participant_id = ?", roomID, participantID).First(&roomParticipant).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New(constant.ErrRoomParticipantNotFound)
		}
		return nil, err
	}
	return mapper.ToRoomParticipantEntity(&roomParticipant), nil
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
