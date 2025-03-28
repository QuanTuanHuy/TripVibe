package port

import (
	"chat_service/core/domain/dto/request"
	"chat_service/core/domain/dto/response"
	"chat_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IMessagePort interface {
	CreateMessage(ctx context.Context, tx *gorm.DB, message *entity.MessageEntity) (*entity.MessageEntity, error)
	GetMessagesByRoomId(ctx context.Context, chatRoomID int64, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error)
	GetMessagesByIDs(ctx context.Context, messageIDs []int64) ([]*entity.MessageEntity, error)
	MarkMessageAsRead(ctx context.Context, tx *gorm.DB, roomID, userID, messageID int64) error
	CountUnreadMessages(ctx context.Context, roomID, userID int64) (int64, error)
}
