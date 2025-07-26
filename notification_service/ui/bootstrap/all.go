package bootstrap

import (
	"context"
	"notification_service/core/domain/constant"
	"notification_service/core/port"
	"notification_service/core/service"
	"notification_service/core/usecase"
	"notification_service/core/woker"
	adapter2 "notification_service/infrastructure/cache/adapter"
	"notification_service/infrastructure/client"
	"notification_service/infrastructure/kafka"
	"notification_service/infrastructure/repository/adapter"
	"notification_service/kernel/properties"
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

		// Provide properties
		golib.ProvideProps(properties.NewEmailProperties),
		golib.ProvideProps(properties.NewInternalServiceProperties),
		golib.ProvideProps(properties.NewKafkaProperties),
		golib.ProvideProps(properties.NewEmailConsumerProperties),

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

func NewApiClient(
	emailProperties *properties.EmailProperties,
	internalService *properties.InternalServiceProperties) *client.ApiClient {
	emailHeaders := map[string]string{
		"api-key": emailProperties.ApiKey,
	}

	internalServiceOptions := make([]client.ApiClientOption, 0, len(internalService.Services))
	for _, service := range internalService.Services {
		internalServiceOptions = append(internalServiceOptions, client.WithService(service.Name, service.Uri+service.ContextPath, 10*time.Second))
		internalServiceOptions = append(internalServiceOptions, client.WithServiceRetry(service.Name, 3, 500*time.Millisecond))
	}

	apiClientOptions := []client.ApiClientOption{
		client.WithDefaultHeaders(map[string]string{
			"Content-Type": "application/json",
			"X-API-Source": "notification-service",
		}),

		client.WithService(constant.EMAIL_SENDER, emailProperties.Url, 10*time.Second),
		client.WithServiceRetry(constant.EMAIL_SENDER, 3, 500*time.Millisecond),
		client.WithServiceHeaders(constant.EMAIL_SENDER, emailHeaders),
	}

	apiClientOptions = append(apiClientOptions, internalServiceOptions...)

	apiClient := client.NewApiClient(apiClientOptions...)

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
