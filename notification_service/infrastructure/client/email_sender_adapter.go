package client

import (
	"context"
	"errors"
	"github.com/golibs-starter/golib/log"
	"notification_service/core/domain/dto/request"
	"notification_service/core/port"
)

type EmailSenderAdapter struct {
	apiClient *ApiClient
}

func (e EmailSenderAdapter) SendEmail(ctx context.Context, content *request.SendEmailRequest) error {
	url := "/v3/smtp/email"
	var res string
	err := e.apiClient.PostJSON(ctx, "email_sender", url, content, &res)
	if err != nil {
		log.Error(ctx, "send email failed ", err)
		return err
	}
	if res != "" {
		log.Info(ctx, "send email success ", res)
		return nil
	}
	return errors.New("send email failed ")
}

func NewEmailSenderAdapter(apiClient *ApiClient) port.IEmailSenderPort {
	return &EmailSenderAdapter{
		apiClient: apiClient,
	}
}
