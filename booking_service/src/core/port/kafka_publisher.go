package port

type IKafkaPublisher interface {
	PushMessage(payload any, topic string) error
}
