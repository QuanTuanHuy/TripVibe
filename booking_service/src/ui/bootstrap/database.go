package bootstrap

import (
	"booking_service/infrastructure/repository/model"
	"github.com/golibs-starter/golib/log"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

func RunDatabase() {
	dsn := "host=localhost user=user_booking password=secret dbname=db_booking_service port=5433 sslmode=disable"
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})

	if err != nil {
		log.Fatal("Failed to connect to database", err)
	}
	err = db.AutoMigrate(
		&model.AccommodationModel{},
		&model.UnitModel{},
		&model.BookingModel{},
		&model.BookingUnitModel{},
		&model.BookingPromotionModel{}, &model.UserModel{},
		&model.QuickBookingModel{})
	if err != nil {
		log.Fatal("Failed to migrate database", err)
	}
}
