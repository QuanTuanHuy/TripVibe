package kafka

import (
	"context"
	"encoding/json"
	"notification_service/core/domain/entity"
	"time"

	"github.com/golibs-starter/golib/log"
	"github.com/segmentio/kafka-go"
)

// EmailProducer handles sending email notification events to Kafka
type EmailProducer struct {
	writer *kafka.Writer
}

// EmailNotificationEvent represents an email notification to be processed
type EmailNotificationEvent struct {
	NotificationID int64                  `json:"notification_id"`
	UserID         int64                  `json:"user_id"`
	Recipient      string                 `json:"recipient"`
	Subject        string                 `json:"subject"`
	Content        string                 `json:"content"`
	Metadata       map[string]interface{} `json:"metadata"`
	CreatedAt      int64                  `json:"created_at"`
}

// NewEmailProducer creates a new Kafka email producer
func NewEmailProducer(brokers []string) *EmailProducer {
	writer := &kafka.Writer{
		Addr:         kafka.TCP(brokers...),
		Topic:        "email-notifications",
		Balancer:     &kafka.LeastBytes{},
		BatchTimeout: 10 * time.Millisecond,
		// Enable idempotent writes to prevent duplicate messages
		RequiredAcks: kafka.RequireAll,
	}

	return &EmailProducer{
		writer: writer,
	}
}

// SendEmailNotification sends a notification to the Kafka topic
func (p *EmailProducer) SendEmailNotification(ctx context.Context, notification *entity.NotificationEntity) error {
	event := EmailNotificationEvent{
		NotificationID: notification.ID,
		UserID:         notification.UserID,
		Recipient:      notification.Recipient,
		Subject:        notification.Title,
		Content:        notification.Content,
		Metadata:       notification.Metadata,
		CreatedAt:      notification.CreatedAt,
	}

	eventJSON, err := json.Marshal(event)
	if err != nil {
		log.Error(ctx, "Failed to marshal email notification event: ", err)
		return err
	}

	message := kafka.Message{
		Key:   []byte(notification.Recipient), // Use recipient as key for partitioning
		Value: eventJSON,
		Time:  time.Now(),
		// Add headers for tracing and metadata
		Headers: []kafka.Header{
			{Key: "notification_type", Value: []byte(notification.Type)},
			{Key: "notification_id", Value: []byte(string(notification.ID))},
		},
	}

	err = p.writer.WriteMessages(ctx, message)
	if err != nil {
		log.Error(ctx, "Failed to write message to Kafka: ", err)
		return err
	}

	log.Info(ctx, "Email notification sent to Kafka topic for processing, ID: ", notification.ID)
	return nil
}

// Close closes the Kafka writer
func (p *EmailProducer) Close() error {
	return p.writer.Close()
}
