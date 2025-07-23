package config

import (
	"fmt"
	"memo_service/src/infrastructure/repository/model"

	"go.uber.org/zap"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

func NewPostgresDB(cfg *AppConfig, logger *zap.Logger) *gorm.DB {
	dsn := fmt.Sprintf("host=%s port=%s user=%s password=%s dbname=%s sslmode=disable",
		cfg.DBHost, cfg.DBPort, cfg.DBUser, cfg.DBPassword, cfg.DBName)

	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		logger.Fatal("Failed to connect to PostgreSQL database", zap.Error(err))
	}

	err = db.AutoMigrate(&model.UserModel{}, &model.MemoModel{})
	if err != nil {
		logger.Fatal("Failed to migrate database", zap.Error(err))
	}

	logger.Info("Connected to PostgreSQL database")
	return db
}
