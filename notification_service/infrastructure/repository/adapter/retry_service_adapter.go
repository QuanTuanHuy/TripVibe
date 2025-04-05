package adapter

import (
	"context"
	"notification_service/core/domain/entity"
	"notification_service/core/port"
	"time"

	"github.com/golibs-starter/golib/log"
	"gorm.io/gorm"
)

// RetryServiceAdapter implements retry functionality for notifications
type RetryServiceAdapter struct {
	db               *gorm.DB
	maxRetryCount    int
	notificationPort port.INotificationPort
}

// NewRetryServiceAdapter creates a new retry service adapter
func NewRetryServiceAdapter(db *gorm.DB, maxRetryCount int, notificationPort port.INotificationPort) port.IRetryServicePort {
	return &RetryServiceAdapter{
		db:               db,
		maxRetryCount:    maxRetryCount,
		notificationPort: notificationPort,
	}
}

// ScheduleRetry attempts to retry a failed notification
func (r *RetryServiceAdapter) ScheduleRetry(ctx context.Context, notificationID int64) bool {
	tx := r.db.Begin()
	defer func() {
		if r := recover(); r != nil {
			tx.Rollback()
		}
	}()

	if tx.Error != nil {
		log.Error(ctx, "Failed to begin transaction: ", tx.Error)
		return false
	}

	// Get the notification
	notification, err := r.notificationPort.GetNotificationByID(ctx, notificationID)
	if err != nil {
		log.Error(ctx, "Failed to get notification for retry: ", err)
		tx.Rollback()
		return false
	}

	// Check if max retries reached
	if notification.RetryCount >= r.maxRetryCount {
		log.Error(ctx, "Max retry count reached for notification: ", notificationID)

		// Update status to FAILED
		notification.Status = entity.StatusFailed

		// Update the notification
		_, err = r.notificationPort.UpdateNotification(ctx, tx, notification)
		if err != nil {
			log.Error(ctx, "Failed to update notification status to FAILED: ", err)
			tx.Rollback()
			return false
		}

		if err := tx.Commit().Error; err != nil {
			log.Error(ctx, "Failed to commit transaction for failed notification: ", err)
			return false
		}

		return false
	}

	// Increment retry count
	notification.RetryCount++

	// Set status to PENDING for retry
	notification.Status = entity.StatusPending

	// Update last retry timestamp
	now := time.Now().Unix()
	notification.LastRetryAt = &now

	// Update the notification
	_, err = r.notificationPort.UpdateNotification(ctx, tx, notification)
	if err != nil {
		log.Error(ctx, "Failed to update notification for retry: ", err)
		tx.Rollback()
		return false
	}

	if err := tx.Commit().Error; err != nil {
		log.Error(ctx, "Failed to commit transaction for retry: ", err)
		return false
	}

	log.Info(ctx, "Scheduled retry for notification: ", notificationID, ", retry count: ", notification.RetryCount)
	return true
}

// MarkAsSent marks a notification as successfully sent
func (r *RetryServiceAdapter) MarkAsSent(ctx context.Context, notificationID int64) error {
	tx := r.db.Begin()
	defer func() {
		if r := recover(); r != nil {
			tx.Rollback()
		}
	}()

	if tx.Error != nil {
		log.Error(ctx, "Failed to begin transaction: ", tx.Error)
		return tx.Error
	}

	// Get the notification
	notification, err := r.notificationPort.GetNotificationByID(ctx, notificationID)
	if err != nil {
		log.Error(ctx, "Failed to get notification to mark as sent: ", err)
		tx.Rollback()
		return err
	}

	// Update status to SENT
	notification.Status = entity.StatusSent

	// Update sent timestamp
	now := time.Now().Unix()
	notification.SentAt = &now

	// Update the notification
	_, err = r.notificationPort.UpdateNotification(ctx, tx, notification)
	if err != nil {
		log.Error(ctx, "Failed to update notification status to SENT: ", err)
		tx.Rollback()
		return err
	}

	if err := tx.Commit().Error; err != nil {
		log.Error(ctx, "Failed to commit transaction for marking notification as sent: ", err)
		return err
	}

	log.Info(ctx, "Marked notification as sent: ", notificationID)
	return nil
}
