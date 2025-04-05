package bootstrap

import (
	"context"
	"notification_service/core/port"
	"notification_service/core/service"
	"notification_service/core/usecase"
	cache_adapter "notification_service/infrastructure/cache/adapter"
	"notification_service/infrastructure/client"
	"notification_service/infrastructure/kafka"
	"notification_service/infrastructure/repository/adapter"
	"notification_service/ui/controller"
	"notification_service/ui/router"
	"notification_service/worker"
	"time"

	"github.com/golibs-starter/golib"
	golibdata "github.com/golibs-starter/golib-data"
	golibgin "github.com/golibs-starter/golib-gin"
	"go.uber.org/fx"
	"gorm.io/gorm"
)

func All() fx.Option {
	return fx.Options(
		golib.AppOpt(),
		golib.PropertiesOpt(),
		golib.LoggingOpt(),
		golib.EventOpt(),
		golib.BuildInfoOpt(Version, CommitHash, BuildTime),
		golib.HttpRequestLogOpt(),

		// Provide datasource auto config
		// redis cache instance
		golibdata.RedisOpt(),
		golibdata.DatasourceOpt(),

		// Provide configurations
		fx.Provide(kafka.NewConfiguration),
		fx.Provide(client.NewEmailConfiguration),

		// Provide port implementation
		fx.Provide(adapter.NewNotificationAdapter),
		fx.Provide(adapter.NewDatabaseTransactionAdapter),
		fx.Provide(cache_adapter.NewRedisCacheAdapter),
		fx.Provide(client.NewUserClientAdapter),

		// Provide Kafka components
		fx.Provide(provideEmailProducer),
		fx.Provide(provideEmailConsumer),
		fx.Provide(provideEmailSender),
		fx.Provide(adapter.NewRetryServiceAdapter),

		// Provide usecase
		fx.Provide(usecase.NewCreateNotificationUseCase),
		fx.Provide(usecase.NewDatabaseTransactionUseCase),
		fx.Provide(usecase.NewGetNotificationUseCase),
		fx.Provide(usecase.NewUpdateNotificationUseCase),
		fx.Provide(usecase.NewGetUserUseCase),
		fx.Provide(usecase.NewEmailNotificationUseCase),

		// Provide services
		fx.Provide(service.NewNotificationService),

		// Provide controller
		fx.Provide(controller.NewNotificationController),

		// Provide gin http server auto config,
		// actuator endpoints and application routers
		golibgin.GinHttpServerOpt(),
		fx.Invoke(router.RegisterGinRouters),

		// Start Kafka consumer
		fx.Invoke(startEmailConsumer),

		// Provide worker components
		fx.Provide(provideFailedNotificationProcessor),

		// Start background workers
		fx.Invoke(startFailedNotificationProcessor),

		golibgin.OnStopHttpServerOpt(),
	)
}

// Provider functions for dependency injection

func provideEmailProducer(config *kafka.Configuration) *kafka.EmailProducer {
	if !config.EmailProducerEnabled {
		return nil
	}
	return kafka.NewEmailProducer(config.Brokers)
}

func provideEmailSender(config *client.EmailConfiguration) port.IEmailSenderPort {
	return client.NewEmailSenderAdapter(
		config.SMTPHost,
		config.SMTPPort,
		config.SMTPUsername,
		config.SMTPPassword,
		config.FromEmail,
	)
}

func provideEmailConsumer(
	config *kafka.Configuration,
	emailSender port.IEmailSenderPort,
	retryService port.IRetryServicePort,
) *kafka.EmailConsumer {
	if !config.EmailConsumerEnabled {
		return nil
	}
	return kafka.NewEmailConsumer(
		config.Brokers,
		config.ConsumerGroupID,
		emailSender,
		retryService,
	)
}

// Start the email consumer if enabled
func startEmailConsumer(lc fx.Lifecycle, consumer *kafka.EmailConsumer, config *kafka.Configuration) {
	if consumer == nil || !config.EmailConsumerEnabled {
		return
	}

	lc.Append(fx.Hook{
		OnStart: func(ctx context.Context) error {
			consumer.Start(ctx)
			return nil
		},
		OnStop: func(ctx context.Context) error {
			consumer.Stop()
			return nil
		},
	})
}

// Provider for the failed notification processor
func provideFailedNotificationProcessor(
	db *gorm.DB,
	emailNotificationUseCase usecase.IEmailNotificationUseCase,
) *worker.FailedNotificationProcessor {
	return worker.NewFailedNotificationProcessor(
		db,
		emailNotificationUseCase,
		24*time.Hour,  // max retry age - retry notifications that failed in the last 24 hours
		5*time.Minute, // processing interval - check for failed notifications every 5 minutes
	)
}

// Start the failed notification processor
func startFailedNotificationProcessor(lc fx.Lifecycle, processor *worker.FailedNotificationProcessor) {
	lc.Append(fx.Hook{
		OnStart: func(ctx context.Context) error {
			processor.Start(ctx)
			return nil
		},
		OnStop: func(ctx context.Context) error {
			processor.Stop()
			return nil
		},
	})
}
