package woker

import (
	"context"
	"notification_service/core/domain/entity"
	"notification_service/core/usecase"
	"time"

	"github.com/golibs-starter/golib/log"
)

// FailedNotificationProcessor handles retrying failed notifications
type FailedNotificationProcessor struct {
	getNotiUseCase           usecase.IGetNotificationUseCase
	emailNotificationUseCase usecase.IEmailNotificationUseCase
	maxRetryAge              time.Duration
	processingInterval       time.Duration
	isRunning                bool
	stopChan                 chan struct{}
}

// NewFailedNotificationProcessor creates a new failed notification processor
func NewFailedNotificationProcessor(
	getNotiUseCase usecase.IGetNotificationUseCase,
	emailNotificationUseCase usecase.IEmailNotificationUseCase,
	maxRetryAge time.Duration,
	processingInterval time.Duration,
) *FailedNotificationProcessor {
	return &FailedNotificationProcessor{
		getNotiUseCase:           getNotiUseCase,
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
	statusList := []string{
		string(entity.StatusPending),
		string(entity.StatusFailed),
	}
	notiType := string(entity.EmailNotification)

	notifications, err := p.getNotiUseCase.GetNotificationToRetry(ctx, notiType, statusList, cutoffTime, 50)
	if err != nil {
		log.Error(ctx, "get notification to retry failed: ", err)
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
