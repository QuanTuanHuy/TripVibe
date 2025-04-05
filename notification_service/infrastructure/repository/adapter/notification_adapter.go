package adapter

import (
	"context"
	"errors"
	"gorm.io/gorm"
	"notification_service/core/domain/constant"
	"notification_service/core/domain/dto/request"
	"notification_service/core/domain/entity"
	"notification_service/core/port"
	"notification_service/infrastructure/repository/mapper"
	"notification_service/infrastructure/repository/model"
	"notification_service/infrastructure/repository/specification"
)

type NotificationAdapter struct {
	base
}

func (n NotificationAdapter) GetNotificationToRetry(ctx context.Context, notiType string, statusList []string, cutoffTime int64, limit int) ([]*entity.NotificationEntity, error) {
	var notiModels []*model.NotificationModel
	if err := n.db.WithContext(ctx).Model(&model.NotificationModel{}).
		Where("type = ? AND status IN (?) AND created_at > ?", notiType, statusList, cutoffTime).
		Limit(limit).
		Find(&notiModels).Error; err != nil {
		return nil, err
	}
	return mapper.ToListNotificationEntity(notiModels), nil
}

func (n NotificationAdapter) GetNotificationByID(ctx context.Context, notificationID int64) (*entity.NotificationEntity, error) {
	var notificationModel model.NotificationModel
	if err := n.db.WithContext(ctx).Model(&model.NotificationModel{}).
		Where("id = ?", notificationID).
		First(&notificationModel).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New(constant.ErrNotificationNotFound)
		}
		return nil, err
	}
	return mapper.ToNotificationEntity(&notificationModel), nil
}

func (n NotificationAdapter) UpdateNotification(ctx context.Context, tx *gorm.DB, notification *entity.NotificationEntity) (*entity.NotificationEntity, error) {
	notificationModel := mapper.ToNotificationModel(notification)
	if err := tx.WithContext(ctx).Model(&model.NotificationModel{}).
		Where("id = ?", notification.ID).
		Updates(notificationModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToNotificationEntity(notificationModel), nil
}

func (n NotificationAdapter) GetAllNotification(ctx context.Context, notificationParams *request.NotificationParams) ([]*entity.NotificationEntity, error) {
	var notificationModels []*model.NotificationModel
	query, args := specification.ToGetNotificationSpecification(notificationParams)
	if err := n.db.WithContext(ctx).
		Raw(query, args...).
		Scan(&notificationModels).Error; err != nil {
		return nil, err
	}

	return mapper.ToListNotificationEntity(notificationModels), nil
}

func (n NotificationAdapter) CountAllNotification(ctx context.Context, notificationParams *request.NotificationParams) (int64, error) {
	query, args := specification.ToCountNotificationSpecification(notificationParams)
	var count int64
	if err := n.db.WithContext(ctx).Model(&model.NotificationModel{}).
		Raw(query, args...).
		Count(&count).
		Error; err != nil {
		return 0, err
	}
	return count, nil
}

func (n NotificationAdapter) CreateNotification(ctx context.Context, tx *gorm.DB, notification *entity.NotificationEntity) (*entity.NotificationEntity, error) {
	notificationModel := mapper.ToNotificationModel(notification)
	if err := tx.WithContext(ctx).
		Create(notificationModel).Error; err != nil {
		return nil, err
	}
	return mapper.ToNotificationEntity(notificationModel), nil
}

func NewNotificationAdapter(db *gorm.DB) port.INotificationPort {
	return &NotificationAdapter{
		base: base{db: db},
	}
}
