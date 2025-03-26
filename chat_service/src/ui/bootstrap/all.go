package bootstrap

import (
	"chat_service/core/service"
	"chat_service/core/usecase"
	"chat_service/infrastructure/repository/adapter"
	"chat_service/ui/controller"
	"chat_service/ui/router"
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
		fx.Provide(adapter.NewChatRoomAdapter),
		fx.Provide(adapter.NewMessageAdapter),
		fx.Provide(adapter.NewParticipantAdapter),
		fx.Provide(adapter.NewRoomParticipantAdapter),
		fx.Provide(adapter.NewDatabaseTransactionAdapter),

		//Provide usecase
		fx.Provide(usecase.NewDatabaseTransactionUseCase),
		fx.Provide(usecase.NewCreateChatRoomUseCase),

		//Provide services
		fx.Provide(service.NewChatRoomService),

		//Provide controller
		fx.Provide(controller.NewChatController),

		//Provide jwt

		//postgres
		fx.Invoke(NewDatabase),

		// Provide gin http server auto config,
		// actuator endpoints and application routers
		golibgin.GinHttpServerOpt(),
		fx.Invoke(router.RegisterGinRouters),

		golibgin.OnStopHttpServerOpt(),
	)
}
