package request

import "notification_service/core/domain/entity"

type CreateNotificationRequestDto struct {
	UserID    int64  `json:"userId"`
	Type      string `json:"type"`
	Title     string `json:"title"`
	Content   string `json:"content"`
	Recipient string `json:"recipient"`
}

func ToNotificationEntity(req *CreateNotificationRequestDto) *entity.NotificationEntity {
	return &entity.NotificationEntity{
		UserID:    req.UserID,
		Type:      entity.NotificationType(req.Type),
		Title:     req.Title,
		Content:   req.Content,
		Recipient: req.Recipient,
		Status:    entity.StatusPending,
	}
}
