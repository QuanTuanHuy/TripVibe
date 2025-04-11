package eventhandler

import (
	"context"
	"encoding/json"
	"notification_service/core/domain/dto/request"
	"notification_service/core/service"
	kafka2 "notification_service/infrastructure/kafka"
	"strings"
	"time"

	"github.com/golibs-starter/golib/log"
	"github.com/segmentio/kafka-go"
)

// EmailConsumer handles consuming and processing email notification events
type EmailConsumer struct {
	reader       *kafka.Reader
	emailService service.IEmailService
	isRunning    bool
	stopChan     chan struct{}
	retryService service.IRetryService
}

// NewEmailConsumer creates a new email consumer
func NewEmailConsumer(
	brokers []string,
	groupID string,
	emailService service.IEmailService,
	retryService service.IRetryService,
) *EmailConsumer {
	// Configure the Kafka reader
	reader := kafka.NewReader(kafka.ReaderConfig{
		Brokers:        brokers,
		Topic:          "notification_service.email",
		GroupID:        groupID,
		MinBytes:       10e3, // 10KB
		MaxBytes:       10e6, // 10MB
		MaxWait:        time.Second,
		CommitInterval: time.Second,
		StartOffset:    kafka.FirstOffset, // Start from oldest message if no offset is stored
	})

	return &EmailConsumer{
		reader:       reader,
		emailService: emailService,
		retryService: retryService,
		stopChan:     make(chan struct{}),
	}
}

// Start begins consuming messages from Kafka
func (c *EmailConsumer) Start(ctx context.Context) {
	if c.isRunning {
		return
	}

	c.isRunning = true
	log.Info(ctx, "Starting email notification consumer")

	go func() {
		defer func() {
			c.isRunning = false
			if err := c.reader.Close(); err != nil {
				log.Error(ctx, "Error closing Kafka reader: ", err)
			}
			log.Info(ctx, "Email notification consumer stopped")
		}()

		for {
			select {
			case <-c.stopChan:
				return
			default:
				message, err := c.reader.ReadMessage(ctx)
				if err != nil {
					if !strings.Contains(err.Error(), "context canceled") {
						log.Error(ctx, "Error reading message from Kafka: ", err)
					}
					continue
				}

				// Process the message
				go c.processMessage(ctx, message)
			}
		}
	}()
}

// Stop stops the consumer
func (c *EmailConsumer) Stop() {
	if !c.isRunning {
		return
	}

	close(c.stopChan)
	log.Info("Stop requested for email consumer but will be ignored to maintain continuous listening")
}

// processMessage handles a single message from Kafka
func (c *EmailConsumer) processMessage(ctx context.Context, message kafka.Message) {
	var event kafka2.EmailNotificationEvent
	if err := json.Unmarshal(message.Value, &event); err != nil {
		log.Error(ctx, "Failed to unmarshal email notification event: ", err)
		return
	}

	log.Info(ctx, "Processing email notification: ", event.NotificationID)

	// Attempt to send the email
	sender := request.EmailInfo{
		Name:  "Booking System",
		Email: "quan.tuan.huy@gmail.com",
	}
	to := request.EmailInfo{
		Name:  event.Recipient,
		Email: event.Recipient,
	}
	err := c.emailService.SendEmail(ctx, &request.SendEmailRequest{
		Sender:      sender,
		To:          []request.EmailInfo{to},
		Subject:     event.Subject,
		HtmlContent: event.Content, // Assuming content is HTML
	})

	if err != nil {
		log.Error(ctx, "Failed to send email notification: ", err)

		// Handle retry logic
		success := c.retryService.ScheduleRetry(ctx, event.NotificationID)
		if !success {
			log.Error(ctx, "Max retry attempts reached for notification: ", event.NotificationID)
		}
		return
	}

	// Update notification status to sent
	err = c.retryService.MarkAsSent(ctx, event.NotificationID)
	if err != nil {
		log.Error(ctx, "Failed to mark notification as sent: ", err)
	}

	log.Info(ctx, "Successfully processed email notification: ", event.NotificationID)
}
