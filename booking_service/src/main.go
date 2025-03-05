package main

import (
	"booking_service/infrastructure/repository/model"
	"booking_service/ui/bootstrap"
	"github.com/golibs-starter/golib/log"
	"go.uber.org/fx"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

func main() {
	// migrate database
	dsn := "host=localhost user=user_booking password=secret dbname=db_booking_service port=5433 sslmode=disable"
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})

	if err != nil {
		log.Fatal("Failed to connect to database", err)
	}
	err = db.AutoMigrate(&model.AccommodationModel{}, &model.UnitModel{})
	if err != nil {
		log.Fatal("Failed to migrate database", err)
	}
	fx.New(bootstrap.All()).Run()
}
