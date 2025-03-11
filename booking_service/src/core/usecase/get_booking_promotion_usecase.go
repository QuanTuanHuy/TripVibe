package usecase

import (
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"context"
)

type IGetBookingPromotionUseCase interface {
	GetBookingPromotionsByBookingID(ctx context.Context, bookingID int64) ([]*entity.BookingPromotionEntity, error)
	GetBookingPromotionsByBookingIDs(ctx context.Context, bookingIDs []int64) ([]*entity.BookingPromotionEntity, error)
}

type GetBookingPromotionUseCase struct {
	bookingPromotionPort port.IBookingPromotionPort
}

func (g GetBookingPromotionUseCase) GetBookingPromotionsByBookingIDs(ctx context.Context, bookingIDs []int64) ([]*entity.BookingPromotionEntity, error) {
	return g.bookingPromotionPort.GetBookingPromotionsByBookingIDs(ctx, bookingIDs)
}

func (g GetBookingPromotionUseCase) GetBookingPromotionsByBookingID(ctx context.Context, bookingID int64) ([]*entity.BookingPromotionEntity, error) {
	return g.bookingPromotionPort.GetBookingPromotionsByBookingID(ctx, bookingID)
}

func NewGetBookingPromotionUseCase(bookingPromotionPort port.IBookingPromotionPort) IGetBookingPromotionUseCase {
	return &GetBookingPromotionUseCase{
		bookingPromotionPort: bookingPromotionPort,
	}
}
