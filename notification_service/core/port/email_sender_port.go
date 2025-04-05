package port

import (
	"context"
)

// EmailContent defines the content of an email to send
type EmailContent struct {
	To      string
	Subject string
	Body    string
	IsHTML  bool
}

// IEmailSenderPort defines the interface for email sending functionality
type IEmailSenderPort interface {
	SendEmail(ctx context.Context, content *EmailContent) error
}

// IRetryServicePort defines the interface for notification retry operations
type IRetryServicePort interface {
	ScheduleRetry(ctx context.Context, notificationID int64) bool
	MarkAsSent(ctx context.Context, notificationID int64) error
}
