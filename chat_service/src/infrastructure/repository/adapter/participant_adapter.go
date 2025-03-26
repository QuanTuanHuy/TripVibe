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

type ParticipantAdapter struct {
	base
}

func (p ParticipantAdapter) CreateParticipant(ctx context.Context, tx *gorm.DB, participant *entity.ParticipantEntity) (*entity.ParticipantEntity, error) {
	participantModel := mapper.ToParticipantModel(participant)
	if err := tx.WithContext(ctx).Create(participantModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToParticipantEntity(participantModel), nil
}

func (p ParticipantAdapter) GetParticipantsById(ctx context.Context, participantId int64) (*entity.ParticipantEntity, error) {
	participantModel := &model.ParticipantModel{}
	if err := p.db.WithContext(ctx).
		Where("user_id = ?", participantId).
		First(participantModel).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New(constant.ErrParticipantNotFound)
		}
		return nil, err
	}
	return mapper.ToParticipantEntity(participantModel), nil
}

func NewParticipantAdapter(db *gorm.DB) port.IParticipantPort {
	return &ParticipantAdapter{
		base: base{
			db: db,
		},
	}
}
