package mapper

import (
	"notification_service/core/domain/entity"
	"notification_service/infrastructure/repository/model"
)

func ToNotificationModel(notification *entity.NotificationEntity) *model.NotificationModel {
	return &model.NotificationModel{
		BaseModel: model.BaseModel{
			ID: notification.ID,
		},
		Type:       notification.Type,
		Content:    notification.Content,
		ReceiverID: notification.ReceiverID,
		IsRead:     notification.IsRead,
		Status:     notification.Status,
	}
}

func ToNotificationEntity(notification *model.NotificationModel) *entity.NotificationEntity {
	return &entity.NotificationEntity{
		BaseEntity: entity.BaseEntity{
			ID:        notification.ID,
			CreatedAt: notification.CreatedAt.Unix(),
			UpdatedAt: notification.UpdatedAt.Unix(),
		},
		Type:       notification.Type,
		Content:    notification.Content,
		ReceiverID: notification.ReceiverID,
		IsRead:     notification.IsRead,
		Status:     notification.Status,
	}
}
