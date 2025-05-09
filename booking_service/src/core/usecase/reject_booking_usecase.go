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
	bookingPort          port.IBookingPort
	inventoryPort        port.IInventoryPort
	accommodationPort    port.IAccommodationPort
	cachePort            port.ICachePort
	dbTransactionUseCase IDatabaseTransactionUseCase
	sendEmailUseCase     ISendEmailUseCase
}

func (r RejectBookingUseCase) RejectBooking(ctx context.Context, userID, bookingID int64) error {
	booking, err := r.bookingPort.GetBookingByID(ctx, bookingID)
	if err != nil {
		log.Error(ctx, "GetBookingByID error", err)
		return err
	}

	if booking.Status != constant.PENDING {
		log.Error(ctx, "Booking status is not pending ", booking.Status)
		return errors.New(constant.ErrForbiddenRejectBooking)
	}

	accommodation, err := r.accommodationPort.GetAccommodationByID(ctx, booking.AccommodationID)
	if err != nil {
		log.Error(ctx, "GetAccommodationByID error", err)
		return err
	}

	if userID != accommodation.OwnerID {
		log.Error(ctx, "user doesn't have permission to reject booking ", userID)
		return errors.New(constant.ErrForbiddenRejectBooking)
	}

	tx := r.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollBack := r.dbTransactionUseCase.Rollback(tx); errRollBack != nil {
			log.Error(ctx, "Rollback reject booking failed: ", errRollBack)
		} else {
			log.Info(ctx, "Rollback reject booking success")
		}
	}()

	// Cập nhật trạng thái booking
	booking.Status = constant.REJECTED
	_, err = r.bookingPort.UpdateBooking(ctx, tx, booking)
	if err != nil {
		log.Error(ctx, "UpdateBooking error", err)
		return err
	}

	// Giải phóng inventory - hàm ReleaseBooking đã được cập nhật để xử lý quantity
	err = r.inventoryPort.ReleaseBooking(ctx, tx, bookingID)
	if err != nil {
		log.Error(ctx, "ReleaseBooking error", err)
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
	inventoryPort port.IInventoryPort,
	accommodationPort port.IAccommodationPort,
	cachePort port.ICachePort,
	dbTransactionUseCase IDatabaseTransactionUseCase,
	sendEmailUseCase ISendEmailUseCase,
) IRejectBookingUseCase {
	return &RejectBookingUseCase{
		bookingPort:          bookingPort,
		inventoryPort:        inventoryPort,
		accommodationPort:    accommodationPort,
		cachePort:            cachePort,
		dbTransactionUseCase: dbTransactionUseCase,
		sendEmailUseCase:     sendEmailUseCase,
	}
}
