package bootstrap

import (
	"github.com/golibs-starter/golib"
	golibdata "github.com/golibs-starter/golib-data"
	golibgin "github.com/golibs-starter/golib-gin"
	"go.uber.org/fx"
	"notification_service/core/service"
	"notification_service/core/usecase"
	adapter2 "notification_service/infrastructure/cache/adapter"
	"notification_service/infrastructure/client"
	"notification_service/infrastructure/repository/adapter"
	"notification_service/ui/controller"
	"notification_service/ui/router"
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

		//Provide usecase
		fx.Provide(usecase.NewCreateNotificationUseCase),
		fx.Provide(usecase.NewDatabaseTransactionUseCase),
		fx.Provide(usecase.NewGetNotificationUseCase),
		fx.Provide(usecase.NewUpdateNotificationUseCase),
		fx.Provide(usecase.NewGetUserUseCase),

		//Provide services
		fx.Provide(service.NewNotificationService),

		//Provide controller
		fx.Provide(controller.NewNotificationController),

		// Provide gin http server auto config,
		// actuator endpoints and application routers
		golibgin.GinHttpServerOpt(),
		fx.Invoke(router.RegisterGinRouters),

		golibgin.OnStopHttpServerOpt(),
	)
}
