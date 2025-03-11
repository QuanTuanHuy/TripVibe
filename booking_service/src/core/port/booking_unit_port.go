package port

import (
	"booking_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IBookingUnitPort interface {
	CreateBookingUnits(ctx context.Context, tx *gorm.DB, bookingUnits []*entity.BookingUnitEntity) ([]*entity.BookingUnitEntity, error)
	GetBookingUnitsByBookingID(ctx context.Context, bookingID int64) ([]*entity.BookingUnitEntity, error)
}
