package usecase

import (
	"context"
	"github.com/golibs-starter/golib/log"
	"notification_service/core/domain/entity"
	"notification_service/core/port"
)

// IEmailNotificationUseCase handles sending notifications via email
type IEmailNotificationUseCase interface {
	QueueEmailNotification(ctx context.Context, notification *entity.NotificationEntity) error
}

// EmailNotificationUseCase implements email notification functionality using Kafka
type EmailNotificationUseCase struct {
	notiPublisher port.INotificationPublisher
}

// NewEmailNotificationUseCase creates a new email notification use case
func NewEmailNotificationUseCase(notiPublisher port.INotificationPublisher) IEmailNotificationUseCase {
	return &EmailNotificationUseCase{
		notiPublisher: notiPublisher,
	}
}

// QueueEmailNotification sends an email notification to the Kafka queue
func (e *EmailNotificationUseCase) QueueEmailNotification(ctx context.Context, notification *entity.NotificationEntity) error {
	// Validate that this is an email notification
	if notification.Type != entity.EmailNotification {
		log.Error(ctx, "Attempted to queue non-email notification to email queue: ", notification.ID)
		return nil
	}

	// Set status to processing
	notification.Status = entity.StatusProcessing

	// Send to Kafka queue
	err := e.notiPublisher.SendEmailNotification(ctx, notification)
	if err != nil {
		log.Error(ctx, "Failed to queue email notification: ", err)
		return err
	}

	log.Info(ctx, "Successfully queued email notification: ", notification.ID)
	return nil
}
