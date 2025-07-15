package entity

type KafkaMessage struct {
	Topic string

	Key []byte

	Value []byte

	Headers map[string][]byte

	TimeStamp int64

	Partition *int32

	Offset int64
}

type MessageHandler func(msg *KafkaMessage) error
