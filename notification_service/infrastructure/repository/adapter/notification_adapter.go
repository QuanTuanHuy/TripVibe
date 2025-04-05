package adapter

import (
	"context"
	"errors"
	"notification_service/core/domain/entity"
	"notification_service/core/port"
	"notification_service/infrastructure/repository/model"
	"time"

	"github.com/golibs-starter/golib/log"
	"gorm.io/gorm"
)

// NotificationAdapter implements the notification data operations
type NotificationAdapter struct {
	db *gorm.DB
}

// NewNotificationAdapter creates a new notification adapter
func NewNotificationAdapter(db *gorm.DB) port.INotificationPort {
	return &NotificationAdapter{
		db: db,
	}
}

// GetNotificationByID retrieves a notification by ID
func (n *NotificationAdapter) GetNotificationByID(ctx context.Context, id int64) (*entity.NotificationEntity, error) {
	var notificationModel model.Notification

	result := n.db.First(&notificationModel, id)
	if result.Error != nil {
		if errors.Is(result.Error, gorm.ErrRecordNotFound) {
			return nil, errors.New("notification not found")
		}
		log.Error(ctx, "Failed to get notification: ", result.Error)
		return nil, result.Error
	}

	return n.mapModelToEntity(&notificationModel), nil
}

// UpdateNotification updates an existing notification
func (n *NotificationAdapter) UpdateNotification(ctx context.Context, db *gorm.DB, notification *entity.NotificationEntity) (*entity.NotificationEntity, error) {
	// Create model from entity
	notificationModel := n.mapEntityToModel(notification)

	// Set updated timestamp
	notificationModel.UpdatedAt = time.Now().Unix()

	// Use the provided transaction or the default DB
	tx := n.db
	if db != nil {
		tx = db
	}

	// Update in database
	result := tx.Save(&notificationModel)
	if result.Error != nil {
		log.Error(ctx, "Failed to update notification: ", result.Error)
		return nil, result.Error
	}

	return n.mapModelToEntity(&notificationModel), nil
}

// mapModelToEntity converts from database model to domain entity
func (n *NotificationAdapter) mapModelToEntity(model *model.Notification) *entity.NotificationEntity {
	return &entity.NotificationEntity{
		ID:          model.ID,
		Type:        model.Type,
		Title:       model.Title,
		Content:     model.Content,
		UserID:      model.UserID,
		Recipient:   model.Recipient,
		Metadata:    model.Metadata,
		Status:      model.Status,
		RetryCount:  model.RetryCount,
		SentAt:      model.SentAt,
		LastRetryAt: model.LastRetryAt,
		CreatedAt:   model.CreatedAt,
		UpdatedAt:   model.UpdatedAt,
	}
}

// mapEntityToModel converts from domain entity to database model
func (n *NotificationAdapter) mapEntityToModel(entity *entity.NotificationEntity) model.Notification {
	return model.Notification{
		ID:          entity.ID,
		Type:        entity.Type,
		Title:       entity.Title,
		Content:     entity.Content,
		UserID:      entity.UserID,
		Recipient:   entity.Recipient,
		Metadata:    entity.Metadata,
		Status:      entity.Status,
		RetryCount:  entity.RetryCount,
		SentAt:      entity.SentAt,
		LastRetryAt: entity.LastRetryAt,
		CreatedAt:   entity.CreatedAt,
		UpdatedAt:   entity.UpdatedAt,
	}
}
