package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"booking_service/core/domain/mapper"
	"booking_service/core/port"
	"context"
	"errors"
	"github.com/golibs-starter/golib/log"
	"slices"
)

type ICreateBookingUseCase interface {
	CreateBooking(ctx context.Context, req *request.CreateBookingDto) (*entity.BookingEntity, error)
}

type CreateBookingUseCase struct {
	bookingPort          port.IBookingPort
	bookingUnitPort      port.IBookingUnitPort
	bookingPromotionPort port.IBookingPromotionPort
	userPort             port.IUserPort
	promotionPort        port.IPromotionPort
	getAccUseCase        IGetAccommodationUseCase
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (c CreateBookingUseCase) CreateBooking(ctx context.Context, req *request.CreateBookingDto) (*entity.BookingEntity, error) {
	// verify accommodation with units valid
	accommodation, err := c.getAccUseCase.GetAccommodationByID(ctx, req.AccommodationID)
	if err != nil {
		log.Error(ctx, "GetAccommodationByID error ", err)
		return nil, err
	}
	allUnitIds := make([]int64, 0)
	for _, unit := range accommodation.Units {
		allUnitIds = append(allUnitIds, unit.ID)
	}

	for _, unit := range req.Units {
		if !slices.Contains(allUnitIds, unit.UnitID) {
			log.Error(ctx, "Unit not found ", unit.UnitID)
			return nil, errors.New(constant.ErrUnitNotFound)
		}
	}

	// verify promotion
	allPromotionIds := make([]int64, 0)
	for _, promotion := range req.Promotions {
		allPromotionIds = append(allPromotionIds, promotion.PromotionID)
	}
	if len(allPromotionIds) > 0 {
		verifyPromotionReq := &request.VerifyPromotionRequest{
			AccommodationID: accommodation.ID,
			PromotionIDs:    allPromotionIds,
		}
		verifyPromotionRes, err := c.promotionPort.VerifyPromotion(ctx, verifyPromotionReq)
		if err != nil {
			log.Error(ctx, "VerifyPromotion error ", err)
			return nil, err
		}
		if !verifyPromotionRes.IsValid {
			log.Error(ctx, "Promotion not valid ")
			return nil, errors.New(constant.ErrPromotionNotValid)
		}
	}

	tx := c.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := c.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback create booking failed, : ", errRollBack)
		} else {
			log.Info(ctx, "Rollback create booking success")
		}
	}()

	// create tourist info
	existedUser, err := c.userPort.GetUserByID(ctx, req.Tourist.TouristID)
	if err != nil && err.Error() != constant.ErrUserNotFound {
		log.Error(ctx, "GetUserByID error ", err)
		return nil, err
	}
	if existedUser != nil {
		// update user info
		mapper.ToUserEntityUpdate(existedUser, req.Tourist)
		existedUser, err = c.userPort.UpdateUserByID(ctx, tx, existedUser)
		if err != nil {
			log.Error(ctx, "UpdateUserByID error ", err)
			return nil, err
		}
	} else {
		// create user info
		newUser := mapper.ToUserEntity(req.Tourist)
		newUser, err = c.userPort.CreateUser(ctx, tx, newUser)
	}

	// create booking
	booking := mapper.ToBookingEntity(req)
	booking.Status = constant.PENDING
	booking, err = c.bookingPort.CreateBooking(ctx, tx, booking)
	if err != nil {
		log.Error(ctx, "CreateBooking error ", err)
		return nil, err
	}

	// create booking unit
	bookingUnits := make([]*entity.BookingUnitEntity, 0)
	for _, unit := range req.Units {
		bookingUnit := mapper.ToBookingUnitEntity(unit, booking.ID)
		bookingUnits = append(bookingUnits, bookingUnit)
	}
	bookingUnits, err = c.bookingUnitPort.CreateBookingUnits(ctx, tx, bookingUnits)
	if err != nil {
		log.Error(ctx, "CreateBookingUnits error ", err)
		return nil, err
	}
	booking.Units = bookingUnits

	// create booking promotion
	bookingPromotions := make([]*entity.BookingPromotionEntity, 0)
	for _, promotion := range req.Promotions {
		bookingPromotion := mapper.ToBookingPromotion(promotion, booking.ID)
		bookingPromotions = append(bookingPromotions, bookingPromotion)
	}
	bookingPromotions, err = c.bookingPromotionPort.CreateBookingPromotions(ctx, tx, bookingPromotions)
	if err != nil {
		log.Error(ctx, "CreateBookingPromotions error ", err)
		return nil, err
	}
	booking.Promotions = bookingPromotions

	errCommit := c.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit create booking failed, : ", errCommit)
		return nil, errCommit
	}

	return booking, nil
}

func NewCreateBookingUseCase(
	bookingPort port.IBookingPort,
	bookingUnitPort port.IBookingUnitPort,
	bookingPromotionPort port.IBookingPromotionPort,
	userPort port.IUserPort,
	promotionPort port.IPromotionPort,
	getAccUseCase IGetAccommodationUseCase,
	dbTransactionUseCase IDatabaseTransactionUseCase) ICreateBookingUseCase {
	return &CreateBookingUseCase{
		bookingPort:          bookingPort,
		bookingUnitPort:      bookingUnitPort,
		bookingPromotionPort: bookingPromotionPort,
		userPort:             userPort,
		promotionPort:        promotionPort,
		getAccUseCase:        getAccUseCase,
		dbTransactionUseCase: dbTransactionUseCase,
	}
}
