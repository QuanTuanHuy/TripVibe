package port

import (
	"context"
	"notification_service/core/domain/dto/request"
)

// IEmailSenderPort defines the interface for email sending functionality
type IEmailSenderPort interface {
	SendEmail(ctx context.Context, content *request.SendEmailRequest) error
}
