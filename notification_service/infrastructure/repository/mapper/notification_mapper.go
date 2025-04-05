package mapper

import (
	"encoding/json"
	"notification_service/core/domain/entity"
	"notification_service/infrastructure/repository/model"
	"time"
)

func ToNotificationModel(notification *entity.NotificationEntity) *model.NotificationModel {
	metadata, _ := json.Marshal(notification.Metadata)
	return &model.NotificationModel{
		BaseModel: model.BaseModel{
			ID:        notification.ID,
			CreatedAt: time.Unix(notification.CreatedAt, 0),
			UpdatedAt: time.Unix(notification.UpdatedAt, 0),
		},
		UserID:      notification.UserID,
		Type:        string(notification.Type),
		Title:       notification.Title,
		Content:     notification.Content,
		Recipient:   notification.Recipient,
		Status:      string(notification.Status),
		Metadata:    string(metadata),
		RetryCount:  notification.RetryCount,
		LastRetryAt: toTime(notification.LastRetryAt),
		SentAt:      toTime(notification.SentAt),
	}
}

func ToNotificationEntity(notification *model.NotificationModel) *entity.NotificationEntity {
	var metadata map[string]interface{}
	_ = json.Unmarshal([]byte(notification.Metadata), &metadata)
	return &entity.NotificationEntity{
		BaseEntity: entity.BaseEntity{
			ID:        notification.ID,
			CreatedAt: notification.CreatedAt.Unix(),
			UpdatedAt: notification.UpdatedAt.Unix(),
		},
		UserID:      notification.UserID,
		Type:        entity.NotificationType(notification.Type),
		Title:       notification.Title,
		Content:     notification.Content,
		Recipient:   notification.Recipient,
		Status:      entity.NotificationStatus(notification.Status),
		Metadata:    metadata,
		RetryCount:  notification.RetryCount,
		LastRetryAt: toUnix(notification.LastRetryAt),
		SentAt:      toUnix(notification.SentAt),
	}
}

func ToListNotificationEntity(notifications []*model.NotificationModel) []*entity.NotificationEntity {
	if notifications == nil {
		return nil
	}
	var entities []*entity.NotificationEntity
	for _, notification := range notifications {
		entities = append(entities, ToNotificationEntity(notification))
	}
	return entities
}

func toTime(unixTime *int64) *time.Time {
	if unixTime == nil {
		return nil
	}
	t := time.Unix(*unixTime, 0)
	return &t
}

func toUnix(t *time.Time) *int64 {
	if t == nil {
		return nil
	}
	unixTime := t.Unix()
	return &unixTime
}
