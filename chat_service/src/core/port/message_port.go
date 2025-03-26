package port

import (
	"chat_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IMessagePort interface {
	CreateMessage(ctx context.Context, tx *gorm.DB, message *entity.MessageEntity) (*entity.MessageEntity, error)
}
