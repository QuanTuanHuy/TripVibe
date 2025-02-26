package adapter

import (
	"context"
	"gorm.io/gorm"
	"notification_service/core/domain/entity"
	"notification_service/core/port"
	"notification_service/infrastructure/repository/mapper"
)

type NotificationAdapter struct {
	base
}

func (n NotificationAdapter) CreateNotification(ctx context.Context, tx *gorm.DB, notification *entity.NotificationEntity) (*entity.NotificationEntity, error) {
	notificationModel := mapper.ToNotificationModel(notification)
	if err := tx.WithContext(ctx).Create(notificationModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToNotificationEntity(notificationModel), nil
}

func NewNotificationAdapter(db *gorm.DB) port.INotificationPort {
	return &NotificationAdapter{
		base: base{db: db},
	}
}
