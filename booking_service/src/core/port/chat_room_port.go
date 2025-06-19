package port

import (
	"booking_service/core/domain/dto/request"
	"context"
)

type IChatRoomPort interface {
	CreateChatRoom(ctx context.Context, req *request.CreateChatRoomDto) (any, error)
}
