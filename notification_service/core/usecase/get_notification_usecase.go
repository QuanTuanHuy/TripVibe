package usecase

import (
	"context"
	"notification_service/core/domain/dto/request"
	"notification_service/core/domain/entity"
	"notification_service/core/port"
)

type IGetNotificationUseCase interface {
	GetAllNotification(ctx context.Context, userID int64, notificationParams *request.NotificationParams) ([]*entity.NotificationEntity, error)
	CountAllNotification(ctx context.Context, userID int64, notificationParams *request.NotificationParams) (int64, error)
}

type GetNotificationUseCase struct {
	notificationPort port.INotificationPort
}

func (g GetNotificationUseCase) GetAllNotification(ctx context.Context, userID int64, notificationParams *request.NotificationParams) ([]*entity.NotificationEntity, error) {
	notificationParams.UserID = &userID
	return g.notificationPort.GetAllNotification(ctx, notificationParams)
}

func (g GetNotificationUseCase) CountAllNotification(ctx context.Context, userID int64, notificationParams *request.NotificationParams) (int64, error) {
	notificationParams.UserID = &userID
	return g.notificationPort.CountAllNotification(ctx, notificationParams)
}

func NewGetNotificationUseCase(notificationPort port.INotificationPort) IGetNotificationUseCase {
	return &GetNotificationUseCase{
		notificationPort: notificationPort,
	}
}
