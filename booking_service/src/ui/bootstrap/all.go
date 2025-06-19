package bootstrap

import (
	"booking_service/core/service"
	"booking_service/core/usecase"
	"booking_service/infrastructure/cache"
	"booking_service/infrastructure/client"
	"booking_service/infrastructure/kafka"
	"booking_service/infrastructure/repository/adapter"
	"booking_service/ui/controller"
	consumer "booking_service/ui/kafka"
	"booking_service/ui/middleware"
	"booking_service/ui/router"
	"context"

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
		fx.Provide(adapter.NewAccommodationAdapter),
		fx.Provide(adapter.NewUnitAdapter),
		fx.Provide(adapter.NewBookingAdapter),
		fx.Provide(adapter.NewBookingUnitAdapter),
		fx.Provide(adapter.NewBookingPromotionAdapter),
		fx.Provide(adapter.NewUserAdapter),
		fx.Provide(cache.NewRedisCacheAdapter),
		fx.Provide(adapter.NewDatabaseTransactionAdapter),
		fx.Provide(client.NewPromotionClientAdapter),
		fx.Provide(client.NewInventoryClientAdapter),
		fx.Provide(client.NewNotificationClientAdapter),
		fx.Provide(adapter.NewQuickBookingAdapter),
		fx.Provide(client.NewChatRoomClientAdapter),

		//Provide usecase
		fx.Provide(usecase.NewDatabaseTransactionUseCase),
		fx.Provide(usecase.NewCreateAccommodationUseCase),
		fx.Provide(usecase.NewGetAccommodationUseCase),
		fx.Provide(usecase.NewDeleteAccommodationUseCase),
		fx.Provide(usecase.NewUpdateAccommodationUseCase),
		fx.Provide(usecase.NewCreateBookingUseCase),
		fx.Provide(usecase.NewGetBookingUseCase),
		fx.Provide(usecase.NewGetUserUseCase),
		fx.Provide(usecase.NewGetBookingPromotionUseCase),
		fx.Provide(usecase.NewGetBookingUnitUseCase),
		fx.Provide(usecase.NewUpdateBookingUseCase),
		fx.Provide(usecase.NewDeleteUnitUseCase),
		fx.Provide(usecase.NewCreateQuickBookingUseCase),
		fx.Provide(usecase.NewUpdateQuickBookingUseCase),
		fx.Provide(usecase.NewDeleteQuickBookingUseCase),
		fx.Provide(usecase.NewGetQuickBookingUseCase),
		fx.Provide(usecase.NewCancelBookingUseCase),
		fx.Provide(usecase.NewRejectBookingUseCase),
		fx.Provide(usecase.NewApproveBookingUseCase),
		fx.Provide(usecase.NewConfirmBookingUseCase),
		fx.Provide(usecase.NewSendEmailUseCase),
		fx.Provide(usecase.NewCheckInBookingUseCase),
		fx.Provide(usecase.NewCheckOutBookingUseCase),

		//Provide services
		fx.Provide(service.NewAccommodationService),
		fx.Provide(service.NewBookingService),
		fx.Provide(service.NewQuickBookingService),

		//Provide controller
		fx.Provide(controller.NewAccommodationController),
		fx.Provide(controller.NewBookingController),
		fx.Provide(controller.NewInternalBookingController),
		fx.Provide(controller.NewQuickBookingController),

		//Provide jwt
		fx.Provide(middleware.NewJWTConfig),

		//postgres
		fx.Invoke(RunDatabase),

		// Provide gin http server auto config,
		// actuator endpoints and application routers
		golibgin.GinHttpServerOpt(),
		fx.Invoke(router.RegisterGinRouters),

		golibgin.OnStopHttpServerOpt(),

		//Kafka
		fx.Provide(kafka.NewConsumerGroupHandler),
		fx.Provide(consumer.NewAccommodationHandler),
		fx.Provide(consumer.NewAccommodationEventHandler),

		// Run Kafka consumer in its own goroutine using FX lifecycle hooks.
		fx.Invoke(func(lc fx.Lifecycle, consumerGroupHandler *kafka.MyConsumerGroupHandler) {
			lc.Append(fx.Hook{
				OnStart: func(ctx context.Context) error {
					go kafka.ConsumerGroup(consumerGroupHandler)
					return nil
				},
				OnStop: func(ctx context.Context) error {
					return nil
				},
			})
		}),
	)
}
