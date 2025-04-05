package usecase

import (
	"context"
	"notification_service/core/domain/entity"
	"notification_service/infrastructure/kafka"

	"github.com/golibs-starter/golib/log"
)

// IEmailNotificationUseCase handles sending notifications via email
type IEmailNotificationUseCase interface {
	QueueEmailNotification(ctx context.Context, notification *entity.NotificationEntity) error
}

// EmailNotificationUseCase implements email notification functionality using Kafka
type EmailNotificationUseCase struct {
	emailProducer *kafka.EmailProducer
}

// NewEmailNotificationUseCase creates a new email notification use case
func NewEmailNotificationUseCase(emailProducer *kafka.EmailProducer) IEmailNotificationUseCase {
	return &EmailNotificationUseCase{
		emailProducer: emailProducer,
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
	err := e.emailProducer.SendEmailNotification(ctx, notification)
	if err != nil {
		log.Error(ctx, "Failed to queue email notification: ", err)
		return err
	}

	log.Info(ctx, "Successfully queued email notification: ", notification.ID)
	return nil
}
