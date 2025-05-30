package port

import (
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IBookingPort interface {
	CreateBooking(ctx context.Context, tx *gorm.DB, booking *entity.BookingEntity) (*entity.BookingEntity, error)
	GetBookingByID(ctx context.Context, ID int64) (*entity.BookingEntity, error)
	UpdateBooking(ctx context.Context, tx *gorm.DB, booking *entity.BookingEntity) (*entity.BookingEntity, error)
	GetBookingByUserIDAndID(ctx context.Context, userID int64, ID int64) (*entity.BookingEntity, error)
	GetAllBookings(ctx context.Context, params *request.BookingParams) ([]*entity.BookingEntity, error)
	CountAllBookings(ctx context.Context, params *request.BookingParams) (int64, error)
}
