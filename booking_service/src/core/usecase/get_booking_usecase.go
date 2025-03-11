package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/entity"
	"booking_service/core/port"
	"booking_service/kernel/utils"
	"context"
	"encoding/json"
	"github.com/golibs-starter/golib/log"
)

type IGetBookingUseCase interface {
	GetDetailBooking(ctx context.Context, userID int64, bookingID int64) (*entity.BookingEntity, error)
}

type GetBookingUseCase struct {
	bookingPort                port.IBookingPort
	getBookingUnitUseCase      IGetBookingUnitUseCase
	getBookingPromotionUseCase IGetBookingPromotionUseCase
	getUserUseCase             IGetUserUseCase
	cachePort                  port.ICachePort
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
