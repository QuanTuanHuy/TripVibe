package adapter

import (
	"chat_service/core/domain/entity"
	"chat_service/core/port"
	"chat_service/infrastructure/repository/mapper"
	"context"
	"gorm.io/gorm"
)

type MessageAdapter struct {
	base
}

func (m MessageAdapter) CreateMessage(ctx context.Context, tx *gorm.DB, message *entity.MessageEntity) (*entity.MessageEntity, error) {
	messageModel := mapper.ToMessageModel(message)
	if err := tx.WithContext(ctx).Create(messageModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToMessageEntity(messageModel), nil
}

func NewMessageAdapter(db *gorm.DB) port.IMessagePort {
	return &MessageAdapter{
		base: base{
			db: db,
		},
	}
}
