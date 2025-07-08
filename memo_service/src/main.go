package main

import (
	"context"
	"memo_service/src/config"
	"memo_service/src/core/service"
	"memo_service/src/core/usecase"
	"memo_service/src/infrastructure/repository/adapter"
	"memo_service/src/ui/controller"
	"memo_service/src/ui/router"

	"github.com/gin-gonic/gin"
	"github.com/redis/go-redis/v9"
	"go.uber.org/fx"
	"go.uber.org/zap"
)

func main() {
	app := fx.New(
		fx.Provide(config.NewLogger),
		fx.Provide(config.NewConfig),
		fx.Provide(config.NewPostgresDB),
		fx.Provide(config.NewRedisClient),
		fx.Provide(config.NewGinEngine),
		fx.Provide(config.NewMarkdown),

		fx.Provide(adapter.NewMemoAdapter),
		fx.Provide(adapter.NewUserAdapter),
		fx.Provide(adapter.NewDBTransactionAdapter),

		fx.Provide(usecase.NewGetUserUseCase),
		fx.Provide(usecase.NewCreateMemoUsecase),
		fx.Provide(usecase.NewGetMemoUsecase),

		fx.Provide(service.NewMemoService),
		fx.Provide(service.NewMarkdownService),

		fx.Provide(controller.NewMemoController),
		fx.Provide(controller.NewMarkdownController),

		fx.Invoke(router.RegisterGinRouters),
		fx.Invoke(startServer),
		fx.Invoke(func(redis *redis.Client) {}),
	)

	app.Run()
}

func startServer(lc fx.Lifecycle, engine *gin.Engine, cfg *config.AppConfig, logger *zap.Logger) {
	lc.Append(fx.Hook{
		OnStart: func(ctx context.Context) error {
			go func() {
				if err := engine.Run(":" + cfg.Port); err != nil {
					logger.Fatal("Failed to start server", zap.Error(err))
				}
				logger.Info("Server started", zap.String("port", cfg.Port))
			}()
			return nil
		},
		OnStop: func(ctx context.Context) error {
			logger.Info("Server stopped")
			return nil
		},
	})
}
