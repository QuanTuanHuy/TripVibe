package mapper

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/entity"
	"chat_service/infrastructure/repository/model"
)

func ToParticipantEntity(participant *model.ParticipantModel) *entity.ParticipantEntity {
	return &entity.ParticipantEntity{
		UserID:   participant.UserID,
		UserName: participant.UserName,
		Role:     constant.UserRole(participant.Role),
	}
}

func ToParticipantModel(participant *entity.ParticipantEntity) *model.ParticipantModel {
	return &model.ParticipantModel{
		UserID:   participant.UserID,
		UserName: participant.UserName,
		Role:     string(participant.Role),
	}
}

func ToParticipantEntities(participants []*model.ParticipantModel) []*entity.ParticipantEntity {
	var entities []*entity.ParticipantEntity
	for _, p := range participants {
		entities = append(entities, ToParticipantEntity(p))
	}
	return entities
}
