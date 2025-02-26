package main

import (
	"github.com/golibs-starter/golib/log"
	"go.uber.org/fx"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"notification_service/infrastructure/repository/model"
	"notification_service/ui/bootstrap"
)

func main() {
	// migrate database
	dsn := "host=localhost user=user_booking password=secret dbname=db_notification_service port=5433 sslmode=disable"
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})

	if err != nil {
		log.Fatal("Failed to connect to database", err)
	}
	db.AutoMigrate(&model.NotificationModel{})
	fx.New(bootstrap.All()).Run()
}
