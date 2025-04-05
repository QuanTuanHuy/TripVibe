package port

import (
	"booking_service/core/domain/entity"
	"context"
	"gorm.io/gorm"
)

type IBookingPromotionPort interface {
	CreateBookingPromotions(ctx context.Context, tx *gorm.DB, bookingPromotions []*entity.BookingPromotionEntity) ([]*entity.BookingPromotionEntity, error)
	GetBookingPromotionsByBookingID(ctx context.Context, bookingID int64) ([]*entity.BookingPromotionEntity, error)
	GetBookingPromotionsByBookingIDs(ctx context.Context, bookingIDs []int64) ([]*entity.BookingPromotionEntity, error)
}
