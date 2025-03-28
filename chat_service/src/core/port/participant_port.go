package port

import (
	"chat_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IParticipantPort interface {
	CreateParticipant(ctx context.Context, tx *gorm.DB, participant *entity.ParticipantEntity) (*entity.ParticipantEntity, error)
	GetParticipantsByIDs(ctx context.Context, participantIDs []int64) ([]*entity.ParticipantEntity, error)
	GetParticipantByID(ctx context.Context, participantID int64) (*entity.ParticipantEntity, error)
}
