package main

import (
	"notification_service/infrastructure/repository/model"
	"notification_service/ui/bootstrap"

	"github.com/golibs-starter/golib/log"
	"github.com/joho/godotenv"
	"go.uber.org/fx"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

func main() {
	// load environment variables
	err := godotenv.Load()
	if err != nil {
		log.Fatal("Error loading .env file", err)
	}

	// migrate database
	dsn := "host=localhost user=user_booking password=secret dbname=db_notification_service port=5433 sslmode=disable"
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})

	if err != nil {
		log.Fatal("Failed to connect to database", err)
	}
	err = db.AutoMigrate(&model.NotificationModel{})
	fx.New(bootstrap.All()).Run()
}
