package mapper

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/entity"
	"chat_service/infrastructure/repository/model"
)

func ToMessageEntity(message *model.MessageModel) *entity.MessageEntity {
	if message == nil {
		return nil
	}
	return &entity.MessageEntity{
		BaseEntity: entity.BaseEntity{
			ID:        message.ID,
			CreatedAt: message.CreatedAt.Unix(),
			UpdatedAt: message.UpdatedAt.Unix(),
		},
		ChatRoomID: message.ChatRoomID,
		SenderID:   message.SenderID,
		Content:    message.Content,
		Type:       constant.MessageType(message.Type),
		IsRead:     message.IsRead,
	}
}

func ToMessageModel(message *entity.MessageEntity) *model.MessageModel {
	if message == nil {
		return nil
	}

	return &model.MessageModel{
		BaseModel: model.BaseModel{
			ID: message.ID,
		},
		ChatRoomID: message.ChatRoomID,
		SenderID:   message.SenderID,
		Content:    message.Content,
		Type:       string(message.Type),
		IsRead:     message.IsRead,
	}
}

func ToMessageEntities(messages []*model.MessageModel) []*entity.MessageEntity {
	var entities []*entity.MessageEntity
	for _, msg := range messages {
		entities = append(entities, ToMessageEntity(msg))
	}
	return entities
}
