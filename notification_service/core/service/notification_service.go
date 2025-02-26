package service

import (
	"context"
	"notification_service/core/domain/entity"
	"notification_service/core/usecase"
)

type INotificationService interface {
	CreateNotification(ctx context.Context, notification *entity.NotificationEntity) (*entity.NotificationEntity, error)
}

type NotificationService struct {
	createNotificationUseCase usecase.ICreateNotificationUseCase
}

func (n *NotificationService) CreateNotification(ctx context.Context, notification *entity.NotificationEntity) (*entity.NotificationEntity, error) {
	return n.createNotificationUseCase.CreateNotification(ctx, notification)
}

func NewNotificationService(createNotificationUseCase usecase.ICreateNotificationUseCase) INotificationService {
	return &NotificationService{
		createNotificationUseCase: createNotificationUseCase,
	}
}
