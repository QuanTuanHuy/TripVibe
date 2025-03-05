package bootstrap

import (
	"booking_service/core/service"
	"booking_service/core/usecase"
	"booking_service/infrastructure/repository/adapter"
	"booking_service/ui/controller"
	"booking_service/ui/router"
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
		fx.Provide(adapter.NewDatabaseTransactionAdapter),

		//Provide usecase
		fx.Provide(usecase.NewDatabaseTransactionUseCase),
		fx.Provide(usecase.NewCreateAccommodationUseCase),
		fx.Provide(usecase.NewGetAccommodationUseCase),

		//Provide services
		fx.Provide(service.NewAccommodationService),

		//Provide controller
		fx.Provide(controller.NewAccommodationController),

		// Provide gin http server auto config,
		// actuator endpoints and application routers
		golibgin.GinHttpServerOpt(),
		fx.Invoke(router.RegisterGinRouters),

		golibgin.OnStopHttpServerOpt(),
	)
}
