package usecase

import (
	"context"
	"github.com/golibs-starter/golib/log"
	"notification_service/core/domain/entity"
	"notification_service/core/port"
)

type ICreateNotificationUseCase interface {
	CreateNotification(ctx context.Context, notification *entity.NotificationEntity) (*entity.NotificationEntity, error)
}

type CreateNotificationUseCase struct {
	notificationPort     port.INotificationPort
	dbTransactionUseCase IDatabaseTransactionUseCase
	notiPublisher        port.INotificationPublisher
}

func (c CreateNotificationUseCase) CreateNotification(ctx context.Context, notification *entity.NotificationEntity) (*entity.NotificationEntity, error) {
	tx := c.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := c.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback create notification failed, : ", errRollBack)
		} else {
			log.Info(ctx, "Rollback create notification success")
		}
	}()

	notification.Status = entity.StatusPending
	notification, err := c.notificationPort.CreateNotification(ctx, tx, notification)
	if err != nil {
		log.Error(ctx, "Create notification failed: ", err)
		return nil, err
	}

	errCommit := c.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit create notification failed: ", errCommit)
		return nil, errCommit
	}

	// push to kafka
	go func() {
		if notification.Type == entity.EmailNotification {
			err = c.notiPublisher.SendEmailNotification(ctx, notification)
			if err != nil {
				log.Error(ctx, "Send email notification failed: ", err)
			}
		}
	}()

	return notification, nil
}

func NewCreateNotificationUseCase(
	notificationPort port.INotificationPort,
	dbTransactionUseCase IDatabaseTransactionUseCase,
	notiPublisher port.INotificationPublisher) ICreateNotificationUseCase {
	return &CreateNotificationUseCase{
		notificationPort:     notificationPort,
		dbTransactionUseCase: dbTransactionUseCase,
		notiPublisher:        notiPublisher,
	}
}
