package client

import (
	"context"
	"notification_service/core/domain/constant"
	"notification_service/core/domain/dto/request"
	"notification_service/core/port"

	"github.com/golibs-starter/golib/log"
)

type EmailSenderAdapter struct {
	apiClient *ApiClient
}

func (e EmailSenderAdapter) SendEmail(ctx context.Context, content *request.SendEmailRequest) error {
	var res string
	err := e.apiClient.PostJSON(ctx, constant.EMAIL_SENDER, constant.SEND_EMAIL_PATH, content, &res)
	if err != nil {
		log.Error(ctx, "send email failed ", err)
		return err
	}
	return nil
}

func NewEmailSenderAdapter(apiClient *ApiClient) port.IEmailSenderPort {
	return &EmailSenderAdapter{
		apiClient: apiClient,
	}
}
