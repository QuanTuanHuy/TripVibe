package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/port"
	"booking_service/kernel/utils"
	"context"
	"errors"

	"github.com/golibs-starter/golib/log"
)

type IRejectBookingUseCase interface {
	RejectBooking(ctx context.Context, userID, bookingID int64) error
}

type RejectBookingUseCase struct {
	bookingPort             port.IBookingPort
	bookingUnitPort         port.IBookingUnitPort
	inventoryPort           port.IInventoryPort
	cachePort               port.ICachePort
	dbTransactionUseCase    IDatabaseTransactionUseCase
	getAccommodationUseCase IGetAccommodationUseCase
	sendEmailUseCase        ISendEmailUseCase
}

func (r RejectBookingUseCase) RejectBooking(ctx context.Context, userID, bookingID int64) error {
	booking, err := r.bookingPort.GetBookingByID(ctx, bookingID)
	if err != nil {
		return err
	}
	bookingUnits, err := r.bookingUnitPort.GetBookingUnitsByBookingID(ctx, bookingID)
	if err != nil {
		return err
	}

	if booking.Status != constant.PENDING {
		log.Error(ctx, "Booking status is not pending ", booking.Status)
		return errors.New(constant.ErrForbiddenRejectBooking)
	}

	accommodation, err := r.getAccommodationUseCase.GetAccommodationByID(ctx, booking.AccommodationID)
	if err != nil {
		return err
	}
	if userID != accommodation.OwnerID {
		log.Error(ctx, "user doesn't have permission to reject booking ", userID)
		return errors.New(constant.ErrForbiddenRejectBooking)
	}

	// get inventories
	stayFrom, stayTo := utils.EpochSecondToDay(booking.StayFrom), utils.EpochSecondToDay(booking.StayTo)
	log.Info(ctx, "stayFrom: ", stayFrom, " stayTo: ", stayTo)
	bookingUnitIDs := make([]int64, 0)
	bookingQuantityMap := make(map[int64]int)
	for _, unit := range bookingUnits {
		bookingUnitIDs = append(bookingUnitIDs, unit.UnitID)
		bookingQuantityMap[unit.UnitID] = unit.Quantity
	}
	inventories, err := r.inventoryPort.GetInventories(ctx, accommodation, bookingUnitIDs, stayFrom, stayTo)
	if err != nil {
		return err
	}
	log.Info(ctx, "len(inventories): ", len(inventories))

	tx := r.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := r.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback reject booking failed: ", errRollBack)
		} else {
			log.Info(ctx, "Rollback reject booking success")
		}
	}()

	booking.Status = constant.REJECTED
	_, err = r.bookingPort.UpdateBooking(ctx, tx, booking)
	if err != nil {
		log.Error(ctx, "UpdateBooking error", err)
		return err
	}

	// update inventory
	for _, inventory := range inventories {
		inventory.AvailableCount += bookingQuantityMap[inventory.UnitID]
		if inventory.AvailableCount == inventory.TotalCount {
			inventory.Status = constant.AVAILABLE
		} else {
			inventory.Status = constant.PARTIALLY_BOOKED
		}
	}
	err = r.inventoryPort.SaveAll(ctx, tx, inventories)
	if err != nil {
		log.Error(ctx, "SaveAll inventory error", err)
		return err
	}

	errCommit := r.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit reject booking failed: ", errCommit)
		return errCommit
	}

	go func() {
		// Gửi email thông báo
		err = r.sendEmailUseCase.SendBookingEmail(ctx, bookingID,
			"Your booking has been rejected",
			"Your booking has been rejected by the owner. Please check your booking status for more details.")

		// Xóa cache
		err = r.cachePort.DeleteFromCache(ctx, utils.BuildCacheKeyGetBooking(bookingID))
		if err != nil {
			log.Error(ctx, "DeleteFromCache error", err)
		}
	}()

	return nil
}

func NewRejectBookingUseCase(
	bookingPort port.IBookingPort,
	bookingUnitPort port.IBookingUnitPort,
	inventoryPort port.IInventoryPort,
	getAccommodationUseCase IGetAccommodationUseCase,
	cachePort port.ICachePort,
	dbTransactionUseCase IDatabaseTransactionUseCase,
	sendEmailUseCase ISendEmailUseCase,
) IRejectBookingUseCase {
	return &RejectBookingUseCase{
		bookingPort:             bookingPort,
		bookingUnitPort:         bookingUnitPort,
		inventoryPort:           inventoryPort,
		getAccommodationUseCase: getAccommodationUseCase,
		cachePort:               cachePort,
		dbTransactionUseCase:    dbTransactionUseCase,
		sendEmailUseCase:        sendEmailUseCase,
	}
}
