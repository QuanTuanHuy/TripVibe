package usecase

import (
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"context"
)

type IGetBookingUnitUseCase interface {
	GetBookingUnitsByBookingID(ctx context.Context, bookingID int64) ([]*entity.BookingUnitEntity, error)
	GetBookingUnitsByBookingIDs(ctx context.Context, bookingIDs []int64) ([]*entity.BookingUnitEntity, error)
}

type GetBookingUnitUseCase struct {
	bookingUnitPort port.IBookingUnitPort
}

func (g GetBookingUnitUseCase) GetBookingUnitsByBookingIDs(ctx context.Context, bookingIDs []int64) ([]*entity.BookingUnitEntity, error) {
	return g.bookingUnitPort.GetBookingUnitsByBookingIDs(ctx, bookingIDs)
}

func (g GetBookingUnitUseCase) GetBookingUnitsByBookingID(ctx context.Context, bookingID int64) ([]*entity.BookingUnitEntity, error) {
	return g.bookingUnitPort.GetBookingUnitsByBookingID(ctx, bookingID)
}

func NewGetBookingUnitUseCase(bookingUnitPort port.IBookingUnitPort) IGetBookingUnitUseCase {
	return &GetBookingUnitUseCase{bookingUnitPort: bookingUnitPort}
}
