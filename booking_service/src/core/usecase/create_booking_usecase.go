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
	CreateBooking(ctx context.Context, req *request.CreateBookingDto) (*entity.BookingEntity, error)
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

func (c CreateBookingUseCase) CreateBooking(ctx context.Context, req *request.CreateBookingDto) (*entity.BookingEntity, error) {
	// Xác minh accommodation và units có hợp lệ không
	accommodation, err := c.getAccUseCase.GetAccommodationByID(ctx, req.AccommodationID)
	if err != nil {
		log.Error(ctx, "GetAccommodationByID error ", err)
		return nil, err
	}

	allUnitIds := make([]int64, 0)
	for _, unit := range accommodation.Units {
		allUnitIds = append(allUnitIds, unit.ID)
	}

	// Extract requested unitIDs và quantities cho inventory locking
	unitQuantities := make(map[int64]int)
	for _, unit := range req.Units {
		unitQuantities[unit.UnitID] = unit.Quantity

		// Validate that unit exists and has enough quantity
		found := false
		for _, accUnit := range accommodation.Units {
			if accUnit.ID == unit.UnitID {
				found = true
				if accUnit.Quantity < unit.Quantity {
					log.Error(ctx, "Not enough units available", map[string]interface{}{
						"unitID":            unit.UnitID,
						"requestedQuantity": unit.Quantity,
						"availableQuantity": accUnit.Quantity,
					})
					return nil, errors.New(constant.ErrNotEnoughUnitsAvailable)
				}
				break
			}
		}

		if !found {
			log.Error(ctx, "Unit not found ", unit.UnitID)
			return nil, errors.New(constant.ErrUnitNotFound)
		}
	}

	stayFrom, stayTo := utils.EpochMilliToDay(req.StayFrom), utils.EpochMilliToDay(req.StayTo)

	// Kiểm tra tình trạng phòng - trả về số lượng khả dụng cho mỗi unit
	availableQuantities, err := c.inventoryPort.CheckAvailability(ctx, accommodation.ID, unitQuantities, stayFrom, stayTo)
	if err != nil {
		log.Error(ctx, "CheckAvailability error ", err)
		return nil, err
	}

	// Kiểm tra xem có đủ số lượng cho mỗi loại unit không
	for unitID, requestedQuantity := range unitQuantities {
		availableQty := availableQuantities[unitID]
		if availableQty < requestedQuantity {
			log.Error(ctx, "Not enough availability", map[string]interface{}{
				"unitID":    unitID,
				"requested": requestedQuantity,
				"available": availableQty,
			})
			return nil, errors.New(constant.ErrInventoryNoLongerAvailable)
		}
	}

	// Lock tạm thời trong redis
	_, err = c.inventoryPort.LockInventory(ctx, unitQuantities, stayFrom, stayTo)
	if err != nil {
		log.Error(ctx, "Failed to lock inventory", err)
		return nil, err
	}

	// Tạo một function để đảm bảo giải phóng lock khi có lỗi
	defer func() {
		if err != nil {
			c.inventoryPort.ReleaseLock(ctx, unitQuantities, stayFrom, stayTo)
		}
	}()

	// Verify promotion
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

	// Xác nhận đặt phòng và cập nhật inventory
	err = c.inventoryPort.ConfirmBooking(ctx, tx, booking.ID, accommodation.ID, unitQuantities, stayFrom, stayTo)
	if err != nil {
		log.Error(ctx, "Failed to confirm booking in inventory ", err)
		return nil, err
	}

	errCommit := c.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit create booking failed, : ", errCommit)
		return nil, errCommit
	}

	// Update promotion usage
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
