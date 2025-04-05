package service

import (
	"context"
	"notification_service/core/domain/dto/request"
	"notification_service/core/port"
)

type IEmailService interface {
	SendEmail(ctx context.Context, content *request.SendEmailRequest) error
}

type EmailService struct {
	emailSenderPort port.IEmailSenderPort
}

func (e EmailService) SendEmail(ctx context.Context, content *request.SendEmailRequest) error {
	return e.emailSenderPort.SendEmail(ctx, content)
}

func NewEmailService(emailSenderPort port.IEmailSenderPort) IEmailService {
	return &EmailService{
		emailSenderPort: emailSenderPort,
	}
}
