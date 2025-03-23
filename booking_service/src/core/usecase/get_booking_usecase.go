package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"booking_service/kernel/utils"
	"context"
	"encoding/json"
	"errors"
	"github.com/golibs-starter/golib/log"
)

type IGetBookingUseCase interface {
	GetDetailBooking(ctx context.Context, userID int64, bookingID int64) (*entity.BookingEntity, error)
	GetAllBookings(ctx context.Context, params *request.BookingParams) ([]*entity.BookingEntity, error)
	CountAllBookings(ctx context.Context, params *request.BookingParams) (int64, error)
	GetCompletedBookingByUserIdAndUnitId(ctx context.Context, userId, unitId int64) (*entity.BookingEntity, error)
}

type GetBookingUseCase struct {
	bookingPort                port.IBookingPort
	getBookingUnitUseCase      IGetBookingUnitUseCase
	getBookingPromotionUseCase IGetBookingPromotionUseCase
	getUserUseCase             IGetUserUseCase
	cachePort                  port.ICachePort
}

func (g GetBookingUseCase) GetAllBookings(ctx context.Context, params *request.BookingParams) ([]*entity.BookingEntity, error) {
	bookings, err := g.bookingPort.GetAllBookings(ctx, params)
	if err != nil {
		log.Error(ctx, "get all bookings failed ", err)
		return nil, err
	}

	bookingIDs := make([]int64, 0, len(bookings))
	for _, booking := range bookings {
		bookingIDs = append(bookingIDs, booking.ID)
	}

	bookingUnits, err := g.getBookingUnitUseCase.GetBookingUnitsByBookingIDs(ctx, bookingIDs)
	if err != nil {
		log.Error(ctx, "get booking units by booking ids failed ", err)
		return nil, err
	}
	bookingUnitGroup := make(map[int64][]*entity.BookingUnitEntity)
	for _, bookingUnit := range bookingUnits {
		bookingUnitGroup[bookingUnit.BookingID] = append(bookingUnitGroup[bookingUnit.BookingID], bookingUnit)
	}

	bookingPromotions, err := g.getBookingPromotionUseCase.GetBookingPromotionsByBookingIDs(ctx, bookingIDs)
	if err != nil {
		log.Error(ctx, "get booking promotions by booking ids failed ", err)
		return nil, err
	}
	bookingPromotionGroup := make(map[int64][]*entity.BookingPromotionEntity)
	for _, bookingPromotion := range bookingPromotions {
		bookingPromotionGroup[bookingPromotion.BookingID] =
			append(bookingPromotionGroup[bookingPromotion.BookingID], bookingPromotion)
	}

	touristIDs := make([]int64, 0, len(bookings))
	for _, booking := range bookings {
		touristIDs = append(touristIDs, booking.TouristID)
	}
	tourists, err := g.getUserUseCase.GetUsersByIDs(ctx, touristIDs)
	if err != nil {
		log.Error(ctx, "get users by ids failed ", err)
		return nil, err
	}
	touristMap := make(map[int64]*entity.UserEntity)
	for _, tourist := range tourists {
		touristMap[tourist.ID] = tourist
	}

	for _, booking := range bookings {
		booking.Units = bookingUnitGroup[booking.ID]
		booking.Promotions = bookingPromotionGroup[booking.ID]
		booking.Tourist = touristMap[booking.TouristID]
	}

	return bookings, nil
}

func (g GetBookingUseCase) GetCompletedBookingByUserIdAndUnitId(ctx context.Context, userId int64, unitId int64) (*entity.BookingEntity, error) {
	completedStatus := constant.COMPLETED
	params := &request.BookingParams{
		UserID: &userId,
		Status: &completedStatus,
		UnitID: &unitId,
	}

	bookings, err := g.bookingPort.GetAllBookings(ctx, params)
	if err != nil {
		log.Error(ctx, "Get bookings by userId failed ", err)
		return nil, err
	}

	if len(bookings) == 0 {
		return nil, errors.New(constant.ErrBookingNotFound)
	}

	return bookings[0], nil
}

func (g GetBookingUseCase) CountAllBookings(ctx context.Context, params *request.BookingParams) (int64, error) {
	return g.bookingPort.CountAllBookings(ctx, params)
}

func (g GetBookingUseCase) GetDetailBooking(ctx context.Context, userID int64, bookingID int64) (*entity.BookingEntity, error) {
	// get from cache
	cachedBooking, err := g.cachePort.GetFromCache(ctx, utils.BuildCacheKeyGetBooking(bookingID))
	if err != nil && err.Error() != constant.ErrCacheKeyNil {
		log.Error(ctx, "Get Detail Booking failed", err)
		return nil, err
	}
	if cachedBooking != nil {
		response := entity.BookingEntity{}
		err = json.Unmarshal([]byte(cachedBooking.(string)), &response)
		if err != nil {
			log.Error(ctx, "Unmarshal error", err)
			return nil, err
		}
		return &response, nil
	}

	// get from db
	booking, err := g.bookingPort.GetBookingByUserIDAndID(ctx, userID, bookingID)
	if err != nil {
		log.Error(ctx, "get booking by user id and id failed ", err)
		return nil, err
	}

	bookingUnits, err := g.getBookingUnitUseCase.GetBookingUnitsByBookingID(ctx, booking.ID)
	if err != nil {
		log.Error(ctx, "get booking units by booking id failed ", err)
		return nil, err
	}

	bookingPromotions, err := g.getBookingPromotionUseCase.GetBookingPromotionsByBookingID(ctx, booking.ID)
	if err != nil {
		log.Error(ctx, "get booking promotions by booking id failed ", err)
		return nil, err
	}

	tourist, err := g.getUserUseCase.GetUserByID(ctx, booking.TouristID)
	if err != nil {
		log.Error(ctx, "get user by id failed ", err)
		return nil, err
	}

	booking.Units = bookingUnits
	booking.Promotions = bookingPromotions
	booking.Tourist = tourist

	// set to cache
	go func() {
		err = g.cachePort.SetToCache(ctx, utils.BuildCacheKeyGetBooking(booking.ID), booking, constant.DefaultCacheTTL)
		if err != nil {
			log.Error(ctx, "SetToCache error", err)
		}
	}()

	return booking, nil
}

func NewGetBookingUseCase(bookingPort port.IBookingPort,
	getBookingUnitUseCase IGetBookingUnitUseCase,
	getBookingPromotionUseCase IGetBookingPromotionUseCase,
	getUserUseCase IGetUserUseCase,
	cachePort port.ICachePort) IGetBookingUseCase {
	return &GetBookingUseCase{
		bookingPort:                bookingPort,
		getBookingUnitUseCase:      getBookingUnitUseCase,
		getBookingPromotionUseCase: getBookingPromotionUseCase,
		getUserUseCase:             getUserUseCase,
		cachePort:                  cachePort,
	}
}
