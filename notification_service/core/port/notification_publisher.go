package port

import (
	"context"
	"notification_service/core/domain/entity"
)

type INotificationPublisher interface {
	SendEmailNotification(ctx context.Context, notification *entity.NotificationEntity) error
}
