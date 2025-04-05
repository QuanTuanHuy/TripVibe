package service

import (
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/dto/response"
	"booking_service/core/domain/entity"
	"booking_service/core/usecase"
	"booking_service/kernel/utils"
	"context"

	"github.com/golibs-starter/golib/log"
)

type IBookingService interface {
	CreateBooking(ctx context.Context, req *request.CreateBookingDto) (*entity.BookingEntity, error)
	GetDetailBooking(ctx context.Context, userID int64, bookingID int64) (*entity.BookingEntity, error)
	GetAllBookings(ctx context.Context, params *request.BookingParams) (*response.GetBookingResponse, error)
	ApproveBooking(ctx context.Context, userID int64, bookingID int64) error
	RejectBooking(ctx context.Context, userID int64, bookingID int64) error
	GetCompletedBookingByUserIdAndUnitId(ctx context.Context, userId int64, unitId int64) (*entity.BookingEntity, error)
}

type BookingService struct {
	createBookingUseCase usecase.ICreateBookingUseCase
	getBookingUseCase    usecase.IGetBookingUseCase
	updateBookingUseCase usecase.IUpdateBookingUseCase
}

func (b BookingService) ApproveBooking(ctx context.Context, userID int64, bookingID int64) error {
	return b.updateBookingUseCase.ApproveBooking(ctx, userID, bookingID)
}

func (b BookingService) RejectBooking(ctx context.Context, userID int64, bookingID int64) error {
	return b.updateBookingUseCase.RejectBooking(ctx, userID, bookingID)
}

func (b BookingService) GetAllBookings(ctx context.Context, params *request.BookingParams) (*response.GetBookingResponse, error) {
	bookings, err := b.getBookingUseCase.GetAllBookings(ctx, params)
	if err != nil {
		log.Error(ctx, "GetAllBookings error ", err)
		return nil, err
	}

	total, err := b.getBookingUseCase.CountAllBookings(ctx, params)
	if err != nil {
		log.Error(ctx, "CountAllBookings error ", err)
		return nil, err
	}

	page := int64(*params.Page)
	pageSize := int64(*params.PageSize)
	nextPage, prevPage, totalPage := utils.CalculateParameterForGetRequest(page, pageSize, total)
	return response.ToGetBookingResponse(bookings, page, pageSize, totalPage, total, prevPage, nextPage), nil
}

func (b BookingService) GetDetailBooking(ctx context.Context, userID int64, bookingID int64) (*entity.BookingEntity, error) {
	return b.getBookingUseCase.GetDetailBooking(ctx, userID, bookingID)
}

func (b BookingService) CreateBooking(ctx context.Context, req *request.CreateBookingDto) (*entity.BookingEntity, error) {
	return b.createBookingUseCase.CreateBooking(ctx, req)
}

func (b BookingService) GetCompletedBookingByUserIdAndUnitId(ctx context.Context, userId int64, unitId int64) (*entity.BookingEntity, error) {
	return b.getBookingUseCase.GetCompletedBookingByUserIdAndUnitId(ctx, userId, unitId)
}

func NewBookingService(createBookingUseCase usecase.ICreateBookingUseCase,
	getBookingUseCase usecase.IGetBookingUseCase,
	updateBookingUseCase usecase.IUpdateBookingUseCase) IBookingService {
	return &BookingService{
		createBookingUseCase: createBookingUseCase,
		getBookingUseCase:    getBookingUseCase,
		updateBookingUseCase: updateBookingUseCase,
	}
}
