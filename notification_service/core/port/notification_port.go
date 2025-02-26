package port

import (
	"context"
	"gorm.io/gorm"
	"notification_service/core/domain/dto/request"
	"notification_service/core/domain/entity"
)

type INotificationPort interface {
	CreateNotification(ctx context.Context, tx *gorm.DB, notification *entity.NotificationEntity) (*entity.NotificationEntity, error)
	GetAllNotification(ctx context.Context, notificationParams *request.NotificationParams) ([]*entity.NotificationEntity, error)
	CountAllNotification(ctx context.Context, notificationParams *request.NotificationParams) (int64, error)
	GetNotificationByID(ctx context.Context, notificationID int64) (*entity.NotificationEntity, error)
	UpdateNotification(ctx context.Context, tx *gorm.DB, notification *entity.NotificationEntity) (*entity.NotificationEntity, error)
}
