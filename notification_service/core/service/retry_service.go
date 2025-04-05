package service

import (
	"context"
	"github.com/golibs-starter/golib/log"
	"notification_service/core/domain/dto/request"
	"notification_service/core/domain/entity"
	"notification_service/core/usecase"
	"time"
)

// IRetryService defines the interface for notification retry operations
type IRetryService interface {
	ScheduleRetry(ctx context.Context, notificationID int64) bool
	MarkAsSent(ctx context.Context, notificationID int64) error
}

type RetryService struct {
	maxRetryCount             int
	getNotificationUseCase    usecase.IGetNotificationUseCase
	updateNotificationUseCase usecase.IUpdateNotificationUseCase
}

func (r RetryService) ScheduleRetry(ctx context.Context, notificationID int64) bool {
	notification, err := r.getNotificationUseCase.GetNotificationByID(ctx, notificationID)
	if err != nil {
		log.Error(ctx, "get notification by ID failed: ", err)
		return false
	}

	if notification.RetryCount >= r.maxRetryCount {
		log.Info(ctx, "notification %d has reached max retry count", notificationID)

		newStatus := string(entity.StatusFailed)
		updateDto := &request.UpdateNotificationDto{
			Status: &newStatus,
		}
		_, err = r.updateNotificationUseCase.UpdateNotification(ctx, notificationID, updateDto)
		if err != nil {
			log.Error(ctx, "update notification failed: ", err)
			return false
		}

		return false
	}

	newStatus := string(entity.StatusPending)
	newRetryCount := notification.RetryCount + 1
	now := time.Now().Unix()

	updateDto := &request.UpdateNotificationDto{
		Status:      &newStatus,
		RetryCount:  &newRetryCount,
		LastRetryAt: &now,
	}
	_, err = r.updateNotificationUseCase.UpdateNotification(ctx, notificationID, updateDto)
	if err != nil {
		log.Error(ctx, "update notification failed: ", err)
		return false
	}

	log.Info(ctx, "Scheduled retry for notification: ", notificationID, ", retry count: ", newRetryCount)
	return true

}

func (r RetryService) MarkAsSent(ctx context.Context, notificationID int64) error {
	_, err := r.getNotificationUseCase.GetNotificationByID(ctx, notificationID)
	if err != nil {
		log.Error(ctx, "get notification by ID failed: ", err)
		return err
	}

	newStatus := string(entity.StatusSent)
	now := time.Now().Unix()
	updateDto := &request.UpdateNotificationDto{
		Status: &newStatus,
		SentAt: &now,
	}
	_, err = r.updateNotificationUseCase.UpdateNotification(ctx, notificationID, updateDto)
	if err != nil {
		log.Error(ctx, "update notification status to sent failed: ", err)
		return err
	}

	log.Info(ctx, "Marked as sent for notification: ", notificationID)

	return nil
}

func NewRetryService(
	maxRetryCount int,
	getNotificationUseCase usecase.IGetNotificationUseCase,
	updateNotificationUseCase usecase.IUpdateNotificationUseCase,
) IRetryService {
	return &RetryService{
		maxRetryCount:             maxRetryCount,
		getNotificationUseCase:    getNotificationUseCase,
		updateNotificationUseCase: updateNotificationUseCase,
	}
}
