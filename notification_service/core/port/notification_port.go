package port

import (
	"context"
	"gorm.io/gorm"
	"notification_service/core/domain/entity"
)

type INotificationPort interface {
	CreateNotification(ctx context.Context, tx *gorm.DB, notification *entity.NotificationEntity) (*entity.NotificationEntity, error)
}
