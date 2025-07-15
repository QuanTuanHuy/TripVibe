package port

import (
	"context"

	entity "github.com/quantuanhuy/lib/src/core/entity/msg"
)

type IKafkaProducerPort interface {
	SendMessage(ctx context.Context, message *entity.KafkaMessage) error

	SendMessageAsync(ctx context.Context, message *entity.KafkaMessage, successCallback func(*entity.KafkaMessage), errorCallBack func(*entity.KafkaMessage, error)) error

	SendMessages(ctx context.Context, messages []*entity.KafkaMessage) error

	SendMessagesAsync(ctx context.Context, messages []*entity.KafkaMessage, successCallback func(*entity.KafkaMessage), errorCallBack func(*entity.KafkaMessage, error)) error

	SendMessageToPartition(ctx context.Context, message *entity.KafkaMessage, partition int32) error

	BeginTransaction(ctx context.Context) error

	CommitTransaction(ctx context.Context) error

	AbortTransaction(ctx context.Context) error

	Flush(ctx context.Context) error

	Close() error
}
