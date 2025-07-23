package config

import (
	"github.com/spf13/viper"
	"go.uber.org/zap"
)

type AppConfig struct {
	Port       string `mapstructure:"SERVER_PORT"`
	RedisAddr  string `mapstructure:"REDIS_ADDR"`
	DBHost     string `mapstructure:"DB_HOST"`
	DBPort     string `mapstructure:"DB_PORT"`
	DBUser     string `mapstructure:"DB_USER"`
	DBPassword string `mapstructure:"DB_PASSWORD"`
	DBName     string `mapstructure:"DB_NAME"`
}

func NewConfig(logger *zap.Logger) *AppConfig {
	viper.SetConfigFile(".env")
	viper.AutomaticEnv()

	if err := viper.ReadInConfig(); err != nil {
		logger.Fatal("Failed to read config file", zap.Error(err))
	}

	var config AppConfig
	if err := viper.Unmarshal(&config); err != nil {
		logger.Fatal("Failed to unmarshal config", zap.Error(err))
	}

	return &config
}
