package client

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/port"
	"context"
	"github.com/golibs-starter/golib/log"
	"time"
)

type NotificationClientAdapter struct {
	apiClient *ApiClient
}

func (n NotificationClientAdapter) SendNotification(ctx context.Context, createNotiDto *request.CreateNotificationDto) error {
	var result interface{}
	err := n.apiClient.PostJSON(ctx, constant.NOTIFICATION_SERVICE, constant.SEND_NOTIFICATION_ENPOINT, createNotiDto, result)
	if err != nil {
		log.Error(ctx, "SendNotification error ", err)
		return err
	}
	return nil
}

func NewNotificationClientAdapter() port.INotificationPort {
	apiClient := NewApiClient(
		WithService(constant.NOTIFICATION_SERVICE, constant.NOTIFICATION_SERVICE_URL, 10*time.Second),
		WithServiceRetry(constant.NOTIFICATION_SERVICE, 3, 500*time.Millisecond),
	)
	return &NotificationClientAdapter{
		apiClient: apiClient,
	}
}
