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
	ConfirmBooking(ctx context.Context, bookingID int64) (*response.ConfirmBookingResponse, error)
	ApproveBooking(ctx context.Context, userID, bookingID int64) error
	RejectBooking(ctx context.Context, userID, bookingID int64) error
	CancelBooking(ctx context.Context, userID int64, bookingID int64) error
	GetCompletedBookingByUserIdAndUnitId(ctx context.Context, userId int64, unitId int64) (*entity.BookingEntity, error)
	CheckInBooking(ctx context.Context, userID int64, req *request.CheckInBookingRequest) (*response.CheckInResponse, error)
	CheckOutBooking(ctx context.Context, userID int64, req *request.CheckOutBookingRequest) (*response.CheckOutResponse, error)
	GetBookingStatisticsForHost(ctx context.Context, userID int64, req *request.BookingStatisticsRequest) (*response.BookingStatisticsResponse, error)
}

type BookingService struct {
	createBookingUseCase   usecase.ICreateBookingUseCase
	getBookingUseCase      usecase.IGetBookingUseCase
	confirmBookingUseCase  usecase.IConfirmBookingUseCase
	rejectBookingUseCase   usecase.IRejectBookingUseCase
	approveBookingUseCase  usecase.IApproveBookingUseCase
	cancelBookingUseCase   usecase.ICancelBookingUseCase
	checkInBookingUseCase  usecase.ICheckInBookingUseCase
	checkOutBookingUseCase usecase.ICheckOutBookingUseCase
}

func (b *BookingService) CheckOutBooking(ctx context.Context, userID int64, req *request.CheckOutBookingRequest) (*response.CheckOutResponse, error) {
	return b.checkOutBookingUseCase.CheckOutBooking(ctx, userID, req)
}

func (b BookingService) CheckInBooking(ctx context.Context, userID int64, req *request.CheckInBookingRequest) (*response.CheckInResponse, error) {
	return b.checkInBookingUseCase.CheckInBooking(ctx, userID, req)
}

func (b BookingService) ConfirmBooking(ctx context.Context, bookingID int64) (*response.ConfirmBookingResponse, error) {
	return b.confirmBookingUseCase.ConfirmBooking(ctx, bookingID)
}

func (b BookingService) CancelBooking(ctx context.Context, userID, bookingID int64) error {
	return b.cancelBookingUseCase.CancelBooking(ctx, userID, bookingID)
}

func (b BookingService) ApproveBooking(ctx context.Context, userID, bookingID int64) error {
	return b.approveBookingUseCase.ApproveBooking(ctx, userID, bookingID)
}

func (b BookingService) RejectBooking(ctx context.Context, userID int64, bookingID int64) error {
	return b.rejectBookingUseCase.RejectBooking(ctx, userID, bookingID)
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
	return b.createBookingUseCase.CreateBookingV2(ctx, req)
}

func (b BookingService) GetCompletedBookingByUserIdAndUnitId(ctx context.Context, userId int64, unitId int64) (*entity.BookingEntity, error) {
	return b.getBookingUseCase.GetCompletedBookingByUserIdAndUnitId(ctx, userId, unitId)
}

func (b BookingService) GetBookingStatisticsForHost(ctx context.Context, userID int64, req *request.BookingStatisticsRequest) (*response.BookingStatisticsResponse, error) {
	return b.getBookingUseCase.GetBookingStatisticsForHost(ctx, userID, req)
}

func NewBookingService(
	createBookingUseCase usecase.ICreateBookingUseCase,
	getBookingUseCase usecase.IGetBookingUseCase,
	rejectBookingUseCase usecase.IRejectBookingUseCase,
	confirmBookingUseCase usecase.IConfirmBookingUseCase,
	approveBookingUseCase usecase.IApproveBookingUseCase,
	checkInBookingUseCase usecase.ICheckInBookingUseCase,
	checkOutBookingUseCase usecase.ICheckOutBookingUseCase,
	cancelBookingUseCase usecase.ICancelBookingUseCase) IBookingService {
	return &BookingService{
		createBookingUseCase:   createBookingUseCase,
		getBookingUseCase:      getBookingUseCase,
		confirmBookingUseCase:  confirmBookingUseCase,
		rejectBookingUseCase:   rejectBookingUseCase,
		approveBookingUseCase:  approveBookingUseCase,
		checkInBookingUseCase:  checkInBookingUseCase,
		checkOutBookingUseCase: checkOutBookingUseCase,
		cancelBookingUseCase:   cancelBookingUseCase,
	}
}
