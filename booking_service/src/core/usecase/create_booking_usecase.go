package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
	"booking_service/core/domain/mapper"
	"booking_service/core/port"
	"booking_service/kernel/utils"
	"context"
	"errors"
	"github.com/golibs-starter/golib/log"
)

type ICreateBookingUseCase interface {
	CreateBookingV2(ctx context.Context, req *request.CreateBookingDto) (*entity.BookingEntity, error)
}

type CreateBookingUseCase struct {
	bookingPort          port.IBookingPort
	bookingUnitPort      port.IBookingUnitPort
	bookingPromotionPort port.IBookingPromotionPort
	userPort             port.IUserPort
	promotionPort        port.IPromotionPort
	inventoryPort        port.IInventoryPort
	getAccUseCase        IGetAccommodationUseCase
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (c CreateBookingUseCase) CreateBookingV2(ctx context.Context, req *request.CreateBookingDto) (*entity.BookingEntity, error) {
	// validate accommodation and units
	accommodation, err := c.getAccUseCase.GetAccommodationByID(ctx, req.AccommodationID)
	if err != nil {
		log.Error(ctx, "GetAccommodationByID error ", err)
		return nil, err
	}
	unitMap := make(map[int64]*entity.UnitEntity)
	for _, unit := range accommodation.Units {
		unitMap[unit.ID] = unit
	}

	for _, unit := range req.Units {
		if _, ok := unitMap[unit.UnitID]; !ok {
			log.Error(ctx, "Unit not found in accommodation ", unit.UnitID)
			return nil, errors.New(constant.ErrUnitNotFound)
		}
		if unit.Quantity > unitMap[unit.UnitID].Quantity {
			log.Error(ctx, "Unit quantity exceeds available quantity ", unit.UnitID)
			return nil, errors.New(constant.ErrUnitQuantityExceedsAvailable)
		}
	}

	// try acquiring lock inventory
	startDate, endDate := utils.EpochSecondToDate(req.StayFrom), utils.EpochSecondToDate(req.StayTo)
	log.Info(ctx, "startDate: ", startDate, " endDate: ", endDate)

	var unitLockRequest []*request.InventoryLockUnitRequest
	for _, unit := range req.Units {
		unitLockRequest = append(unitLockRequest, &request.InventoryLockUnitRequest{
			UnitId:    unit.UnitID,
			Quantity:  unit.Quantity,
			StartDate: startDate,
			EndDate:   endDate,
		})
	}
	lockRequest := &request.InventoryLockRequest{
		AccommodationId:  accommodation.ID,
		UserId:           req.Tourist.TouristID,
		UnitLockRequests: unitLockRequest,
	}
	lockResponse, err := c.inventoryPort.AcquireLock(ctx, lockRequest)
	if err != nil {
		log.Error(ctx, "AcquireLock error ", err)
		return nil, err
	}
	if !lockResponse.Success {
		log.Error(ctx, "Inventory lock failed ", lockResponse.Errors)
		return nil, errors.New(constant.ErrInventoryNoLongerAvailable)
	}
	log.Info(ctx, "Inventory lock success, lockId: ", lockResponse.LockID)

	tx := c.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := c.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback create booking failed, : ", errRollBack)
		} else {
			log.Info(ctx, "Rollback create booking success")
		}
	}()

	// Create tourist info
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

	// Create booking
	booking := mapper.ToBookingEntity(req)
	booking.Status = constant.PENDING
	booking.InventoryLockID = &lockResponse.LockID
	booking, err = c.bookingPort.CreateBooking(ctx, tx, booking)
	if err != nil {
		log.Error(ctx, "CreateBooking error ", err)
		return nil, err
	}

	// Create booking unit
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

	// Create booking promotion
	allPromotionIds := make([]int64, 0)
	for _, promotion := range req.Promotions {
		allPromotionIds = append(allPromotionIds, promotion.PromotionID)
	}
	if len(allPromotionIds) > 0 {
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
	}

	errCommit := c.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit create booking failed, : ", errCommit)
		return nil, errCommit
	}

	go func() {
		if len(allPromotionIds) > 0 {
			err = c.promotionPort.UpdatePromotionUsage(ctx, allPromotionIds)
			if err != nil {
				log.Error(ctx, "UpdatePromotionUsage error ", err)
			}
		}
	}()

	return booking, nil
}

func NewCreateBookingUseCase(
	bookingPort port.IBookingPort,
	bookingUnitPort port.IBookingUnitPort,
	bookingPromotionPort port.IBookingPromotionPort,
	userPort port.IUserPort,
	promotionPort port.IPromotionPort,
	inventoryPort port.IInventoryPort,
	getAccUseCase IGetAccommodationUseCase,
	dbTransactionUseCase IDatabaseTransactionUseCase) ICreateBookingUseCase {
	return &CreateBookingUseCase{
		bookingPort:          bookingPort,
		bookingUnitPort:      bookingUnitPort,
		bookingPromotionPort: bookingPromotionPort,
		userPort:             userPort,
		promotionPort:        promotionPort,
		inventoryPort:        inventoryPort,
		getAccUseCase:        getAccUseCase,
		dbTransactionUseCase: dbTransactionUseCase,
	}
}
