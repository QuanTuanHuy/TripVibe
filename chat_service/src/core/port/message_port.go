package port

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/dto/request"
	"chat_service/core/domain/dto/response"
	"chat_service/core/domain/entity"
	"context"

	"gorm.io/gorm"
)

type IMessagePort interface {
	// Existing methods
	CreateMessage(ctx context.Context, tx *gorm.DB, message *entity.MessageEntity) (*entity.MessageEntity, error)
	GetMessagesByRoomId(ctx context.Context, chatRoomID int64, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error)
	GetMessagesByIDs(ctx context.Context, messageIDs []int64) ([]*entity.MessageEntity, error)
	MarkMessageAsRead(ctx context.Context, tx *gorm.DB, roomID, userID, messageID int64) error
	CountUnreadMessages(ctx context.Context, roomID, userID int64) (int64, error)
	// New status-related methods (Task 1.2.2)

	// UpdateMessage updates an entire message entity in the database
	// Used when business logic validation is handled at UseCase level
	UpdateMessage(ctx context.Context, tx *gorm.DB, message *entity.MessageEntity) error

	// GetUnreadMessages retrieves unread messages for a user in a specific room with pagination
	// Returns messages with status 'sent' or 'delivered' for the specified user
	GetUnreadMessages(ctx context.Context, roomID, userID int64, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error)

	// GetMessagesByStatus retrieves messages by specific status with pagination support
	// Useful for analytics and status-based message queries
	GetMessagesByStatus(ctx context.Context, roomID int64, status constant.MessageStatus, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error)
}
