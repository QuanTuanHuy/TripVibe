package usecase

import (
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"context"
)

type IGetQuickBookingUseCase interface {
	GetQuickBooking(ctx context.Context, userID int64, id int64) (*entity.QuickBookingEntity, error)
	GetQuickBookings(ctx context.Context, userID int64, params *request.QuickBookingParams) ([]*entity.QuickBookingEntity, int64, error)
}

type GetQuickBookingUseCase struct {
	quickBookingPort port.IQuickBookingPort
}

func (g GetQuickBookingUseCase) GetQuickBooking(ctx context.Context, userID int64, id int64) (*entity.QuickBookingEntity, error) {
	return g.quickBookingPort.GetQuickBooking(ctx, id, userID)
}

func (g GetQuickBookingUseCase) GetQuickBookings(ctx context.Context, userID int64, params *request.QuickBookingParams) ([]*entity.QuickBookingEntity, int64, error) {
	return g.quickBookingPort.GetQuickBookings(ctx, userID, params)
}

func NewGetQuickBookingUseCase(quickBookingPort port.IQuickBookingPort) IGetQuickBookingUseCase {
	return &GetQuickBookingUseCase{
		quickBookingPort: quickBookingPort,
	}
}
