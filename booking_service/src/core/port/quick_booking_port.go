package port

import (
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IQuickBookingPort interface {
	CreateQuickBooking(ctx context.Context, tx *gorm.DB, entity *entity.QuickBookingEntity) (*entity.QuickBookingEntity, error)

	UpdateQuickBooking(ctx context.Context, tx *gorm.DB, entity *entity.QuickBookingEntity) (*entity.QuickBookingEntity, error)

	DeleteQuickBooking(ctx context.Context, tx *gorm.DB, id int64, userID int64) error

	GetQuickBooking(ctx context.Context, id int64, userID int64) (*entity.QuickBookingEntity, error)

	GetQuickBookings(ctx context.Context, userID int64, params *request.QuickBookingParams) ([]*entity.QuickBookingEntity, int64, error)

	UpdateLastUsed(ctx context.Context, tx *gorm.DB, id int64, userID int64) error
}
