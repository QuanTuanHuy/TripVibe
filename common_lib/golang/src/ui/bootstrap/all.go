package bootstrap

import (
	"context"

	"github.com/gin-gonic/gin"
	"github.com/quantuanhuy/lib/src/config"
	"github.com/quantuanhuy/lib/src/infrastructure/service"
	"go.uber.org/fx"
	"go.uber.org/zap"
)

func All() fx.Option {
	return fx.Options(
		fx.Provide(config.NewLogger),
		fx.Provide(config.NewConfig),
		fx.Provide(config.NewPostgresDB),
		fx.Provide(config.NewRedisClient),
		fx.Provide(config.NewGinEngine),

		fx.Provide(service.NewRedisService),

		fx.Invoke(startServer),
	)
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
