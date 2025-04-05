package worker

import (
	"context"
	"notification_service/core/domain/entity"
	"notification_service/core/usecase"
	"notification_service/infrastructure/kafka"
	"time"

	"github.com/golibs-starter/golib/log"
	"gorm.io/gorm"
)

// FailedNotificationProcessor handles retrying failed notifications
type FailedNotificationProcessor struct {
	db                       *gorm.DB
	emailProducer            *kafka.EmailProducer
	emailNotificationUseCase usecase.IEmailNotificationUseCase
	maxRetryAge              time.Duration
	processingInterval       time.Duration
	isRunning                bool
	stopChan                 chan struct{}
}

// NewFailedNotificationProcessor creates a new failed notification processor
func NewFailedNotificationProcessor(
	db *gorm.DB,
	emailNotificationUseCase usecase.IEmailNotificationUseCase,
	maxRetryAge time.Duration,
	processingInterval time.Duration,
) *FailedNotificationProcessor {
	return &FailedNotificationProcessor{
		db:                       db,
		emailNotificationUseCase: emailNotificationUseCase,
		maxRetryAge:              maxRetryAge,
		processingInterval:       processingInterval,
		stopChan:                 make(chan struct{}),
	}
}

// Start begins processing failed notifications
func (p *FailedNotificationProcessor) Start(ctx context.Context) {
	if p.isRunning {
		return
	}

	p.isRunning = true
	log.Info(ctx, "Starting failed notification processor")

	go func() {
		ticker := time.NewTicker(p.processingInterval)
		defer ticker.Stop()

		for {
			select {
			case <-p.stopChan:
				log.Info(ctx, "Failed notification processor stopped")
				p.isRunning = false
				return
			case <-ticker.C:
				p.processFailedNotifications(ctx)
			}
		}
	}()
}

// Stop stops the processor
func (p *FailedNotificationProcessor) Stop() {
	if !p.isRunning {
		return
	}

	close(p.stopChan)
}

// processFailedNotifications finds and requeues failed notifications
func (p *FailedNotificationProcessor) processFailedNotifications(ctx context.Context) {
	// Calculate the cutoff time for retrying (e.g., retry notifications that failed within the last 24 hours)
	cutoffTime := time.Now().Add(-p.maxRetryAge).Unix()

	// Find notifications that are in PENDING status or have failed, but still have retry attempts available
	var notifications []*entity.NotificationEntity

	// Find email notifications to retry
	result := p.db.Table("notifications").
		Where("type = ? AND status IN (?, ?) AND retry_count < 3 AND created_at > ?",
			entity.EmailNotification, entity.StatusPending, entity.StatusFailed, cutoffTime).
		Limit(50).
		Find(&notifications)

	if result.Error != nil {
		log.Error(ctx, "Failed to query notifications for retry: ", result.Error)
		return
	}

	log.Info(ctx, "Found ", len(notifications), " failed notifications to retry")

	// Process each notification
	for _, notification := range notifications {
		// Requeue the notification
		err := p.emailNotificationUseCase.QueueEmailNotification(ctx, notification)
		if err != nil {
			log.Error(ctx, "Failed to requeue notification: ", err)
			continue
		}

		log.Info(ctx, "Successfully requeued failed notification: ", notification.ID)
	}
}
