package response

import "notification_service/core/domain/entity"

type NotificationResponse struct {
	ID         int64  `json:"id"`
	Type       string `json:"type"`
	Content    string `json:"content"`
	ReceiverID int64  `json:"receiverId"`
	IsRead     bool   `json:"isRead"`
	Status     string `json:"status"`
}

type GetNotificationResponse struct {
	Notifications []*NotificationResponse `json:"notifications"`
	TotalItems    int64                   `json:"total_items"`
	TotalPages    int64                   `json:"total_pages"`
	CurrentPage   int64                   `json:"current_page"`
	PageSize      int64                   `json:"page_size"`
	PreviousPage  *int64                  `json:"previous_page"`
	NextPage      *int64                  `json:"next_page"`
}

func ToNotificationResponse(notification *entity.NotificationEntity) *NotificationResponse {
	return &NotificationResponse{
		ID:         notification.ID,
		Type:       notification.Type,
		Content:    notification.Content,
		ReceiverID: notification.ReceiverID,
		IsRead:     notification.IsRead,
		Status:     notification.Status,
	}
}

func ToListNotificationResponse(notifications []*entity.NotificationEntity) []*NotificationResponse {
	var notificationResponses []*NotificationResponse
	for _, notification := range notifications {
		notificationResponses = append(notificationResponses, ToNotificationResponse(notification))
	}
	return notificationResponses
}

func ToGetNotificationResponse(notifications []*entity.NotificationEntity, page, pageSize, totalPage, total int64, prevPage, nextPage *int64) *GetNotificationResponse {
	return &GetNotificationResponse{
		Notifications: ToListNotificationResponse(notifications),
		TotalItems:    total,
		TotalPages:    totalPage,
		CurrentPage:   page,
		PageSize:      pageSize,
		PreviousPage:  prevPage,
		NextPage:      nextPage,
	}
}
