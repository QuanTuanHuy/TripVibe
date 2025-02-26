package request

import "notification_service/core/domain/entity"

type CreateNotificationRequestDto struct {
	ReceiverID int64  `json:"receiverId"`
	Type       string `json:"type"`
	Content    string `json:"content"`
	Status     string `json:"status"`
}

func ToNotificationEntity(req *CreateNotificationRequestDto) *entity.NotificationEntity {
	return &entity.NotificationEntity{
		ReceiverID: req.ReceiverID,
		Type:       req.Type,
		Content:    req.Content,
		Status:     req.Status,
	}
}
