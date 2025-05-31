package bootstrap

import (
	"chat_service/infrastructure/repository/model"
	"log"

	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

func NewDatabase() {
	dsn := "host=localhost user=user_booking password=secret dbname=db_chat_service port=5433 sslmode=disable"
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})

	if err != nil {
		log.Fatal("Failed to connect to database", err)
	}
	err = db.AutoMigrate(
		&model.ChatRoomModel{},
		&model.MessageModel{},
		&model.ParticipantModel{},
		&model.RoomParticipantModel{},
		&model.MediaDataModel{},
	)
	if err != nil {
		log.Fatal("Failed to migrate database", err)
	}
}
