package port

import (
	"context"
	"notification_service/core/domain/entity"

	"gorm.io/gorm"
)

// INotificationPort defines the interface for notification data operations
type INotificationPort interface {
	GetNotificationByID(ctx context.Context, id int64) (*entity.NotificationEntity, error)
	UpdateNotification(ctx context.Context, db *gorm.DB, notification *entity.NotificationEntity) (*entity.NotificationEntity, error)
}
