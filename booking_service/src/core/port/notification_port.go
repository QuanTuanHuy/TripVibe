package port

import (
	"booking_service/core/domain/dto/request"
	"context"
)

type INotificationPort interface {
	SendNotification(ctx context.Context, createNotiDto *request.CreateNotificationDto) error
}
