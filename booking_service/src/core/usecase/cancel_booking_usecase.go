package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/port"
	"booking_service/kernel/utils"
	"context"
	"errors"
	"github.com/golibs-starter/golib/log"
)

type ICancelBookingUseCase interface {
	CancelBooking(ctx context.Context, userID, bookingID int64) error
}

type CancelBookingUseCase struct {
	bookingPort          port.IBookingPort
	bookingUnitPort      port.IBookingUnitPort
	getInventoryUseCase  IGetInventoryUseCase
	inventoryPort        port.IInventoryPort
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (c CancelBookingUseCase) CancelBooking(ctx context.Context, userID, bookingID int64) error {
	booking, err := c.bookingPort.GetBookingByID(ctx, bookingID)
	if err != nil {
		return err
	}
	bookingUnits, err := c.bookingUnitPort.GetBookingUnitsByBookingID(ctx, bookingID)
	if err != nil {
		return err
	}

	if booking.TouristID != userID {
		log.Error(ctx, "user not allowed to cancel booking", err)
		return errors.New(constant.ErrForbiddenCancelBooking)
	}
	if booking.Status != constant.PENDING {
		log.Error(ctx, "booking not pending", err)
		return errors.New(constant.ErrForbiddenCancelBooking)
	}

	// get inventories
	stayFrom, stayTo := utils.EpochSecondToDay(booking.StayFrom), utils.EpochSecondToDay(booking.StayTo)
	bookingUnitIDs := make([]int64, 0)
	bookingQuantityMap := make(map[int64]int)
	for _, unit := range bookingUnits {
		bookingUnitIDs = append(bookingUnitIDs, unit.UnitID)
		bookingQuantityMap[unit.UnitID] = unit.Quantity
	}
	inventories, err := c.getInventoryUseCase.GetInventories(ctx, booking.AccommodationID, bookingUnitIDs, stayFrom, stayTo)
	if err != nil {
		return err
	}

	tx := c.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := c.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback cancel booking failed: ", errRollBack)
		} else {
			log.Info(ctx, "Rollback cancel booking success")
		}
	}()

	booking.Status = constant.CANCELLED
	if _, err := c.bookingPort.UpdateBooking(ctx, tx, booking); err != nil {
		log.Error(ctx, "update booking failed", err)
		return err
	}

	// update inventory
	for _, inventory := range inventories {
		inventory.AvailableCount += bookingQuantityMap[inventory.UnitID]
		if inventory.AvailableCount >= inventory.TotalCount {
			inventory.Status = constant.AVAILABLE
		} else {
			inventory.Status = constant.PARTIALLY_BOOKED
		}
	}
	err = c.inventoryPort.SaveAll(ctx, tx, inventories)
	if err != nil {
		log.Error(ctx, "SaveAll inventory error", err)
		return err
	}

	if err := c.dbTransactionUseCase.Commit(tx); err != nil {
		log.Error(ctx, "commit transaction failed", err)
		return err
	}

	return nil
}

func NewCancelBookingUseCase(
	bookingPort port.IBookingPort,
	inventoryPort port.IInventoryPort,
	dbTransactionUseCase IDatabaseTransactionUseCase,
	getInventoryUseCase IGetInventoryUseCase,
) ICancelBookingUseCase {
	return &CancelBookingUseCase{
		bookingPort:          bookingPort,
		inventoryPort:        inventoryPort,
		dbTransactionUseCase: dbTransactionUseCase,
		getInventoryUseCase:  getInventoryUseCase,
	}
}
