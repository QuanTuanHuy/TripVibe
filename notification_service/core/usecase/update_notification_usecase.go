package usecase

import (
	"context"
	"github.com/golibs-starter/golib/log"
	"notification_service/core/domain/dto/request"
	"notification_service/core/domain/entity"
	"notification_service/core/port"
)

type IUpdateNotificationUseCase interface {
	UpdateNotification(ctx context.Context, notiID int64, req *request.UpdateNotificationDto) (*entity.NotificationEntity, error)
}

type UpdateNotificationUseCase struct {
	notificationPort     port.INotificationPort
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (u UpdateNotificationUseCase) UpdateNotification(ctx context.Context, notiID int64, req *request.UpdateNotificationDto) (*entity.NotificationEntity, error) {
	tx := u.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollback := tx.Rollback(); errRollback != nil {
			log.Error("Rollback transaction failed: ", errRollback)
		} else {
			log.Info("Rollback transaction success")
		}
	}()

	var err error
	notification, err := u.notificationPort.GetNotificationByID(ctx, notiID)
	if err != nil {
		log.Error(ctx, "Get notification by ID failed: ", err)
		return nil, err
	}

	if req.Status != nil {
		notification.Status = entity.NotificationStatus(*req.Status)
	}
	if req.RetryCount != nil {
		notification.RetryCount = *req.RetryCount
	}
	if req.LastRetryAt != nil {
		notification.LastRetryAt = req.LastRetryAt
	}
	if req.SentAt != nil {
		notification.SentAt = req.SentAt
	}

	notification, err = u.notificationPort.UpdateNotification(ctx, tx, notification)
	if err != nil {
		log.Error("Update notification failed: ", err)
		return nil, err
	}

	if errCommit := tx.Commit().Error; errCommit != nil {
		log.Error("Commit transaction failed: ", errCommit)
		return nil, errCommit
	}

	return notification, nil
}

func NewUpdateNotificationUseCase(notificationPort port.INotificationPort, dbTransactionUseCase IDatabaseTransactionUseCase) IUpdateNotificationUseCase {
	return &UpdateNotificationUseCase{
		notificationPort:     notificationPort,
		dbTransactionUseCase: dbTransactionUseCase,
	}
}
