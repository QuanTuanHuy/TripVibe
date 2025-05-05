package bootstrap

import (
	"context"
	"notification_service/core/port"
	"notification_service/core/service"
	"notification_service/core/usecase"
	"notification_service/core/woker"
	adapter2 "notification_service/infrastructure/cache/adapter"
	"notification_service/infrastructure/client"
	"notification_service/infrastructure/kafka"
	"notification_service/infrastructure/repository/adapter"
	"notification_service/kernel/utils"
	"notification_service/ui/controller"
	"notification_service/ui/eventhandler"
	"notification_service/ui/router"
	"time"

	"github.com/golibs-starter/golib"
	golibdata "github.com/golibs-starter/golib-data"
	golibgin "github.com/golibs-starter/golib-gin"
	"go.uber.org/fx"
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

		//Provide port implementation
		fx.Provide(adapter.NewNotificationAdapter),
		fx.Provide(adapter.NewDatabaseTransactionAdapter),
		fx.Provide(adapter2.NewRedisCacheAdapter),
		fx.Provide(client.NewUserClientAdapter),
		fx.Provide(client.NewEmailSenderAdapter),
		fx.Provide(func(producer *kafka.EmailProducer) port.INotificationPublisher {
			return producer
		}),

		//Provide usecase
		fx.Provide(usecase.NewCreateNotificationUseCase),
		fx.Provide(usecase.NewDatabaseTransactionUseCase),
		fx.Provide(usecase.NewGetNotificationUseCase),
		fx.Provide(usecase.NewUpdateNotificationUseCase),
		fx.Provide(usecase.NewGetUserUseCase),
		fx.Provide(usecase.NewEmailNotificationUseCase),

		//Provide services
		fx.Provide(service.NewNotificationService),
		fx.Provide(service.NewEmailService),
		fx.Provide(provideRetryService),

		//Provide woker
		fx.Provide(provideFailedNotificationProcessor),
		fx.Invoke(startFailedNotificationProcessor),

		//Provide api client
		fx.Provide(NewApiClient),

		//Provide kafka
		fx.Provide(kafka.NewConfiguration),
		fx.Provide(provideEmailProducer),
		fx.Provide(provideEmailConsumer),

		// Start Kafka consumer
		fx.Invoke(startEmailConsumer),

		//Provide controller
		fx.Provide(controller.NewNotificationController),

		// Provide gin http server auto config,
		// actuator endpoints and application routers
		golibgin.GinHttpServerOpt(),
		fx.Invoke(router.RegisterGinRouters),

		golibgin.OnStopHttpServerOpt(),
	)
}

func NewApiClient() *client.ApiClient {
	emailSenderApiKey := utils.GetEnv("EMAIL_SENDER_API_KEY", "")
	emailSenderUrl := utils.GetEnv("EMAIL_SENDER_URL", "https://api.brevo.com")

	emailHeaders := map[string]string{
		"api-key": emailSenderApiKey,
	}

	// Create API client with initial configuration
	apiClient := client.NewApiClient(
		client.WithService("profile", "http://localhost:8086/profile_service", 10*time.Second),
		client.WithService("email_sender", emailSenderUrl, 10*time.Second),

		// Set default headers for all services
		client.WithDefaultHeaders(map[string]string{
			"Content-Type": "application/json",
			"X-API-Source": "notification-service",
		}),

		// Configure specific service with retry options
		client.WithServiceRetry("profile", 3, 500*time.Millisecond),
		client.WithServiceRetry("email_sender", 3, 500*time.Millisecond),

		// Add custom headers for specific services
		client.WithServiceHeaders("email_sender", emailHeaders),
	)

	return apiClient
}

func provideRetryService(
	getNotificationUseCase usecase.IGetNotificationUseCase,
	updateNotificationUseCase usecase.IUpdateNotificationUseCase,
) service.IRetryService {
	return service.NewRetryService(3, getNotificationUseCase, updateNotificationUseCase)
}

func provideEmailProducer(config *kafka.Configuration) *kafka.EmailProducer {
	if !config.EmailProducerEnabled {
		return nil
	}
	return kafka.NewEmailProducer(config.Brokers)
}

func provideEmailConsumer(
	config *kafka.Configuration,
	emailService service.IEmailService,
	retryService service.IRetryService,
) *eventhandler.EmailConsumer {
	if !config.EmailConsumerEnabled {
		return nil
	}
	return eventhandler.NewEmailConsumer(
		config.Brokers,
		config.ConsumerGroupID,
		emailService,
		retryService,
	)
}

// Start the email consumer if enabled
func startEmailConsumer(lc fx.Lifecycle, consumer *eventhandler.EmailConsumer, config *kafka.Configuration) {
	if consumer == nil || !config.EmailConsumerEnabled {
		return
	}

	lc.Append(fx.Hook{
		OnStart: func(ctx context.Context) error {
			bgCtx := context.Background()
			go consumer.Start(bgCtx)
			return nil
		},
		OnStop: func(ctx context.Context) error {
			//consumer.Stop()
			return nil
		},
	})
}

// Provider for the failed notification processor
func provideFailedNotificationProcessor(
	getNotiUseCase usecase.IGetNotificationUseCase,
	emailNotificationUseCase usecase.IEmailNotificationUseCase,
) *woker.FailedNotificationProcessor {
	return woker.NewFailedNotificationProcessor(
		getNotiUseCase,
		emailNotificationUseCase,
		24*time.Hour,  // max retry age - retry notifications that failed in the last 24 hours
		5*time.Minute, // processing interval - check for failed notifications every 5 minutes
	)
}

// Start the failed notification processor
func startFailedNotificationProcessor(lc fx.Lifecycle, processor *woker.FailedNotificationProcessor) {
	lc.Append(fx.Hook{
		OnStart: func(ctx context.Context) error {
			bgCtx := context.Background()
			go processor.Start(bgCtx)
			return nil
		},
		OnStop: func(ctx context.Context) error {
			//processor.Stop()
			return nil
		},
	})
}
