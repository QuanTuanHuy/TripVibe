package client

import (
	"context"
	"fmt"
	"net/smtp"
	"notification_service/core/port"
)

// EmailSenderAdapter implements the email sending functionality
type EmailSenderAdapter struct {
	smtpHost     string
	smtpPort     string
	smtpUsername string
	smtpPassword string
	fromEmail    string
}

// NewEmailSenderAdapter creates a new email sender adapter
func NewEmailSenderAdapter(smtpHost, smtpPort, smtpUsername, smtpPassword, fromEmail string) port.IEmailSenderPort {
	return &EmailSenderAdapter{
		smtpHost:     smtpHost,
		smtpPort:     smtpPort,
		smtpUsername: smtpUsername,
		smtpPassword: smtpPassword,
		fromEmail:    fromEmail,
	}
}

// SendEmail sends an email using SMTP
func (e *EmailSenderAdapter) SendEmail(ctx context.Context, content *port.EmailContent) error {
	// Set up authentication information
	auth := smtp.PlainAuth("", e.smtpUsername, e.smtpPassword, e.smtpHost)

	// Compose email headers
	to := []string{content.To}
	mime := "MIME-version: 1.0;\nContent-Type: text/plain; charset=\"UTF-8\";\n\n"
	if content.IsHTML {
		mime = "MIME-version: 1.0;\nContent-Type: text/html; charset=\"UTF-8\";\n\n"
	}

	subject := "Subject: " + content.Subject + "\n"
	from := "From: " + e.fromEmail + "\n"

	// Compose the full message
	message := []byte(subject + from + mime + "\n" + content.Body)

	// Connect to the server, authenticate, and send the email
	addr := fmt.Sprintf("%s:%s", e.smtpHost, e.smtpPort)

	// Send the email
	err := smtp.SendMail(addr, auth, e.fromEmail, to, message)

	return err
}
