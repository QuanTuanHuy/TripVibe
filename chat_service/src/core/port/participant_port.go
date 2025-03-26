package port

import (
	"chat_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IParticipantPort interface {
	CreateParticipant(ctx context.Context, tx *gorm.DB, participant *entity.ParticipantEntity) (*entity.ParticipantEntity, error)
	GetParticipantsById(ctx context.Context, participantId int64) (*entity.ParticipantEntity, error)
}
