package mapper

import (
	"chat_service/core/domain/entity"
	"chat_service/infrastructure/repository/model"
)

func ToRoomParticipantEntity(roomParticipant *model.RoomParticipantModel) *entity.RoomParticipantEntity {
	if roomParticipant == nil {
		return nil
	}
	return &entity.RoomParticipantEntity{
		BaseEntity: entity.BaseEntity{
			ID:        roomParticipant.ID,
			CreatedAt: roomParticipant.CreatedAt.Unix(),
			UpdatedAt: roomParticipant.UpdatedAt.Unix(),
		},
		ChatRoomID:    roomParticipant.ChatRoomID,
		ParticipantID: roomParticipant.ParticipantID,
	}
}

func ToRoomParticipantModel(roomParticipant *entity.RoomParticipantEntity) *model.RoomParticipantModel {
	if roomParticipant == nil {
		return nil
	}
	return &model.RoomParticipantModel{
		BaseModel: model.BaseModel{
			ID: roomParticipant.ID,
		},
		ChatRoomID:    roomParticipant.ChatRoomID,
		ParticipantID: roomParticipant.ParticipantID,
	}
}

func ToRoomParticipantEntities(roomParticipants []*model.RoomParticipantModel) []*entity.RoomParticipantEntity {
	var entities []*entity.RoomParticipantEntity
	for _, rp := range roomParticipants {
		entities = append(entities, ToRoomParticipantEntity(rp))
	}
	return entities
}

func ToRoomParticipantModels(roomParticipants []*entity.RoomParticipantEntity) []*model.RoomParticipantModel {
	var models []*model.RoomParticipantModel
	for _, rp := range roomParticipants {
		models = append(models, ToRoomParticipantModel(rp))
	}
	return models
}
