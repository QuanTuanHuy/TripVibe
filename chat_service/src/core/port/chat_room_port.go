package port

import (
	"chat_service/core/domain/dto/request"
	"chat_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IChatRoomPort interface {
	CreateChatRoom(ctx context.Context, tx *gorm.DB, chatRoom *entity.ChatRoomEntity) (*entity.ChatRoomEntity, error)
	UpdateChatRoom(ctx context.Context, tx *gorm.DB, chatRoom *entity.ChatRoomEntity) (*entity.ChatRoomEntity, error)
	GetChatRoomByBookingID(ctx context.Context, bookingID int64) (*entity.ChatRoomEntity, error)
	GetChatRoomByID(ctx context.Context, chatRoomID int64) (*entity.ChatRoomEntity, error)
	GetChatRooms(ctx context.Context, params *request.ChatRoomQueryParams) ([]*entity.ChatRoomEntity, error)
	CountChatRooms(ctx context.Context, params *request.ChatRoomQueryParams) (int64, error)
}
