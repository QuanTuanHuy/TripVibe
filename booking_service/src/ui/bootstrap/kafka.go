package bootstrap

import (
	"booking_service/infrastructure/kafka"
	consumer "booking_service/ui/kafka"
)

func RunKafkaConsumer() {
	handlers := map[string]kafka.MessageHandler{
		"booking_service.accommodation": &consumer.AccommodationHandler{},
	}

	consumerGroupHandler := kafka.NewConsumerGroupHandler("booking_service", handlers)
	topics := []string{"booking_service.accommodation"}

	go func() {
		kafka.ConsumerGroup("localhost:9094", topics, "booking_service", consumerGroupHandler)
	}()
}
