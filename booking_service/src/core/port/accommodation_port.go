package port

import (
	"booking_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IAccommodationPort interface {
	CreateAccommodation(ctx context.Context, tx *gorm.DB, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error)
	GetAccommodationByID(ctx context.Context, ID int64) (*entity.AccommodationEntity, error)
	UpdateAccommodation(ctx context.Context, tx *gorm.DB, accommodation *entity.AccommodationEntity) (*entity.AccommodationEntity, error)
	DeleteAccommodation(ctx context.Context, tx *gorm.DB, ID int64) error
}
