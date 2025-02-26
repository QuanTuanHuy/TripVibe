package bootstrap

import (
	"github.com/golibs-starter/golib"
	golibdata "github.com/golibs-starter/golib-data"
	golibgin "github.com/golibs-starter/golib-gin"
	"go.uber.org/fx"
	"notification_service/core/service"
	"notification_service/core/usecase"
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

		//Provide usecase
		fx.Provide(usecase.NewCreateNotificationUseCase),
		fx.Provide(usecase.NewDatabaseTransactionUseCase),
		fx.Provide(usecase.NewGetNotificationUseCase),

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
