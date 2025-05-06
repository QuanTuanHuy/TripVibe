package service

import (
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"booking_service/core/usecase"
	"context"
)

type IQuickBookingService interface {
	CreateQuickBooking(ctx context.Context, userID int64, req *request.CreateQuickBookingDto) (*entity.QuickBookingEntity, error)
	UpdateQuickBooking(ctx context.Context, userID int64, id int64, req *request.UpdateQuickBookingDto) (*entity.QuickBookingEntity, error)
	DeleteQuickBooking(ctx context.Context, userID int64, id int64) error
	GetQuickBooking(ctx context.Context, userID int64, id int64) (*entity.QuickBookingEntity, error)
	GetQuickBookings(ctx context.Context, userID int64, params *request.QuickBookingParams) ([]*entity.QuickBookingEntity, int64, error)
	OneClickBooking(ctx context.Context, userID int64, req *request.OneClickBookingDto) (*entity.BookingEntity, error)
}

type QuickBookingService struct {
	createQuickBookingUseCase usecase.ICreateQuickBookingUseCase
	updateQuickBookingUseCase usecase.IUpdateQuickBookingUseCase
	deleteQuickBookingUseCase usecase.IDeleteQuickBookingUseCase
	getQuickBookingUseCase    usecase.IGetQuickBookingUseCase
}

func (q QuickBookingService) CreateQuickBooking(ctx context.Context, userID int64, req *request.CreateQuickBookingDto) (*entity.QuickBookingEntity, error) {
	return q.createQuickBookingUseCase.CreateQuickBooking(ctx, userID, req)
}

func (q QuickBookingService) UpdateQuickBooking(ctx context.Context, userID int64, id int64, req *request.UpdateQuickBookingDto) (*entity.QuickBookingEntity, error) {
	return q.updateQuickBookingUseCase.UpdateQuickBooking(ctx, userID, id, req)
}

func (q QuickBookingService) DeleteQuickBooking(ctx context.Context, userID int64, id int64) error {
	return q.deleteQuickBookingUseCase.DeleteQuickBooking(ctx, userID, id)
}

func (q QuickBookingService) GetQuickBooking(ctx context.Context, userID int64, id int64) (*entity.QuickBookingEntity, error) {
	return q.getQuickBookingUseCase.GetQuickBooking(ctx, userID, id)
}

func (q QuickBookingService) GetQuickBookings(ctx context.Context, userID int64, params *request.QuickBookingParams) ([]*entity.QuickBookingEntity, int64, error) {
	return q.getQuickBookingUseCase.GetQuickBookings(ctx, userID, params)
}

func (q QuickBookingService) OneClickBooking(ctx context.Context, userID int64, req *request.OneClickBookingDto) (*entity.BookingEntity, error) {
	//TODO implement me
	panic("implement me")
}

func NewQuickBookingService(
	createQuickBookingUseCase usecase.ICreateQuickBookingUseCase,
	updateQuickBookingUseCase usecase.IUpdateQuickBookingUseCase,
	deleteQuickBookingUseCase usecase.IDeleteQuickBookingUseCase,
	getQuickBookingUseCase usecase.IGetQuickBookingUseCase,
) IQuickBookingService {
	return &QuickBookingService{
		createQuickBookingUseCase: createQuickBookingUseCase,
		updateQuickBookingUseCase: updateQuickBookingUseCase,
		deleteQuickBookingUseCase: deleteQuickBookingUseCase,
		getQuickBookingUseCase:    getQuickBookingUseCase,
	}
}
