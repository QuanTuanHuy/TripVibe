package service

import (
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"booking_service/core/usecase"
	"context"
)

type IBookingService interface {
	CreateBooking(ctx context.Context, req *request.CreateBookingDto) (*entity.BookingEntity, error)
	GetDetailBooking(ctx context.Context, userID int64, bookingID int64) (*entity.BookingEntity, error)
}

type BookingService struct {
	createBookingUseCase usecase.ICreateBookingUseCase
	getBookingUseCase    usecase.IGetBookingUseCase
}

func (b BookingService) GetDetailBooking(ctx context.Context, userID int64, bookingID int64) (*entity.BookingEntity, error) {
	return b.getBookingUseCase.GetDetailBooking(ctx, userID, bookingID)
}

func (b BookingService) CreateBooking(ctx context.Context, req *request.CreateBookingDto) (*entity.BookingEntity, error) {
	return b.createBookingUseCase.CreateBooking(ctx, req)
}

func NewBookingService(createBookingUseCase usecase.ICreateBookingUseCase,
	getBookingUseCase usecase.IGetBookingUseCase) IBookingService {
	return &BookingService{
		createBookingUseCase: createBookingUseCase,
		getBookingUseCase:    getBookingUseCase,
	}
}
