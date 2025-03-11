package service

import (
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"booking_service/core/usecase"
	"context"
)

type IBookingService interface {
	CreateBooking(ctx context.Context, req *request.CreateBookingDto) (*entity.BookingEntity, error)
}

type BookingService struct {
	createBookingUseCase usecase.ICreateBookingUseCase
}

func (b BookingService) CreateBooking(ctx context.Context, req *request.CreateBookingDto) (*entity.BookingEntity, error) {
	return b.createBookingUseCase.CreateBooking(ctx, req)
}

func NewBookingService(createBookingUseCase usecase.ICreateBookingUseCase) IBookingService {
	return &BookingService{
		createBookingUseCase: createBookingUseCase,
	}
}
