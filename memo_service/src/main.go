package main

import (
	"context"
	"memo_service/src/config"

	"github.com/gin-gonic/gin"
	"github.com/redis/go-redis/v9"
	"go.uber.org/fx"
	"go.uber.org/zap"
	"gorm.io/gorm"
)

func main() {
	app := fx.New(
		fx.Provide(config.NewLogger),
		fx.Provide(config.NewConfig),
		fx.Provide(config.NewPostgresDB),
		fx.Provide(config.NewRedisClient),
		fx.Provide(config.NewGinEngine),

		fx.Invoke(startServer),
		fx.Invoke(func(db *gorm.DB) {}),
		fx.Invoke(func(redis *redis.Client) {}),
	)

	app.Run()
}

func startServer(lc fx.Lifecycle, engine *gin.Engine, cfg *config.AppConfig) {
	lc.Append(fx.Hook{
		OnStart: func(ctx context.Context) error {
			go func() {
				if err := engine.Run(":" + cfg.Port); err != nil {
					zap.L().Fatal("Failed to start server", zap.Error(err))
				}
			}()
			return nil
		},
		OnStop: func(ctx context.Context) error {
			zap.L().Info("Server stopped")
			return nil
		},
	})
}
