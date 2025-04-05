package response

import "notification_service/core/domain/entity"

type NotificationResponse struct {
	ID         int64                  `json:"id"`
	Type       string                 `json:"type"`
	Title      string                 `json:"title"`
	Content    string                 `json:"content"`
	Recipient  string                 `json:"recipient"`
	Status     string                 `json:"status"`
	Metadata   map[string]interface{} `json:"metadata"`
	RetryCount int                    `json:"retryCount"`
}

type GetNotificationResponse struct {
	Notifications []*NotificationResponse `json:"notifications"`
	TotalItems    int64                   `json:"totalItems"`
	TotalPages    int64                   `json:"totalPages"`
	CurrentPage   int64                   `json:"currentPage"`
	PageSize      int64                   `json:"pageSize"`
	PreviousPage  *int64                  `json:"previousPage"`
	NextPage      *int64                  `json:"nextPage"`
}

func ToNotificationResponse(notification *entity.NotificationEntity) *NotificationResponse {
	return &NotificationResponse{
		ID:         notification.ID,
		Type:       string(notification.Type),
		Title:      notification.Title,
		Content:    notification.Content,
		Recipient:  notification.Recipient,
		Status:     string(notification.Status),
		Metadata:   notification.Metadata,
		RetryCount: notification.RetryCount,
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
