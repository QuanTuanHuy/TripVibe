package kafka

import (
	"context"
	"encoding/json"
	"notification_service/core/port"
	"strings"
	"time"

	"github.com/golibs-starter/golib/log"
	"github.com/segmentio/kafka-go"
)

// EmailConsumer handles consuming and processing email notification events
type EmailConsumer struct {
	reader       *kafka.Reader
	emailSender  port.IEmailSenderPort
	isRunning    bool
	stopChan     chan struct{}
	retryService port.IRetryServicePort
}

// NewEmailConsumer creates a new email consumer
func NewEmailConsumer(
	brokers []string,
	groupID string,
	emailSender port.IEmailSenderPort,
	retryService port.IRetryServicePort,
) *EmailConsumer {
	// Configure the Kafka reader
	reader := kafka.NewReader(kafka.ReaderConfig{
		Brokers:        brokers,
		Topic:          "email-notifications",
		GroupID:        groupID,
		MinBytes:       10e3, // 10KB
		MaxBytes:       10e6, // 10MB
		MaxWait:        time.Second,
		CommitInterval: time.Second,
		StartOffset:    kafka.FirstOffset, // Start from oldest message if no offset is stored
	})

	return &EmailConsumer{
		reader:       reader,
		emailSender:  emailSender,
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
}

// processMessage handles a single message from Kafka
func (c *EmailConsumer) processMessage(ctx context.Context, message kafka.Message) {
	var event EmailNotificationEvent
	if err := json.Unmarshal(message.Value, &event); err != nil {
		log.Error(ctx, "Failed to unmarshal email notification event: ", err)
		return
	}

	log.Info(ctx, "Processing email notification: ", event.NotificationID)

	// Attempt to send the email
	err := c.emailSender.SendEmail(ctx, &port.EmailContent{
		To:      event.Recipient,
		Subject: event.Subject,
		Body:    event.Content,
		IsHTML:  true, // Assuming content is HTML
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
