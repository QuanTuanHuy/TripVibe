package mapper

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/entity"
	"chat_service/infrastructure/repository/model"
	"time"
)

func ToMessageEntity(message *model.MessageModel) *entity.MessageEntity {
	if message == nil {
		return nil
	}

	var deliveredAt *int64
	if message.DeliveredAt != nil {
		timestamp := message.DeliveredAt.Unix()
		deliveredAt = &timestamp
	}

	var readAt *int64
	if message.ReadAt != nil {
		timestamp := message.ReadAt.Unix()
		readAt = &timestamp
	}

	return &entity.MessageEntity{
		BaseEntity: entity.BaseEntity{
			ID:        message.ID,
			CreatedAt: message.CreatedAt.Unix(),
			UpdatedAt: message.UpdatedAt.Unix(),
		},
		ChatRoomID:  message.ChatRoomID,
		SenderID:    message.SenderID,
		Content:     message.Content,
		Type:        constant.MessageType(message.Type),
		IsRead:      message.IsRead,
		Status:      constant.MessageStatus(message.Status),
		DeliveredAt: deliveredAt,
		ReadAt:      readAt,
	}
}

func ToMessageModel(message *entity.MessageEntity) *model.MessageModel {
	if message == nil {
		return nil
	}

	var deliveredAt *time.Time
	if message.DeliveredAt != nil {
		timestamp := time.Unix(*message.DeliveredAt, 0)
		deliveredAt = &timestamp
	}

	var readAt *time.Time
	if message.ReadAt != nil {
		timestamp := time.Unix(*message.ReadAt, 0)
		readAt = &timestamp
	}

	return &model.MessageModel{
		BaseModel: model.BaseModel{
			ID: message.ID,
		},
		ChatRoomID:  message.ChatRoomID,
		SenderID:    message.SenderID,
		Content:     message.Content,
		Type:        string(message.Type),
		IsRead:      message.IsRead,
		Status:      string(message.Status),
		DeliveredAt: deliveredAt,
		ReadAt:      readAt,
	}
}

func ToMessageEntities(messages []*model.MessageModel) []*entity.MessageEntity {
	var entities []*entity.MessageEntity
	for _, msg := range messages {
		entities = append(entities, ToMessageEntity(msg))
	}
	return entities
}
