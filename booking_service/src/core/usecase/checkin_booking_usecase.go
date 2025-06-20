package usecase

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/dto/response"
	"booking_service/core/port"
	"context"

	"github.com/golibs-starter/golib/log"
)

type ICheckInBookingUseCase interface {
	CheckInBooking(ctx context.Context, userID int64, req *request.CheckInBookingRequest) (*response.CheckInResponse, error)
}

type CheckInBookingUseCase struct {
	bookingPort          port.IBookingPort
	cachePort            port.ICachePort
	dbTransactionUseCase IDatabaseTransactionUseCase
	inventoryPort        port.IInventoryPort
}

func (c CheckInBookingUseCase) CheckInBooking(ctx context.Context, userID int64, req *request.CheckInBookingRequest) (*response.CheckInResponse, error) {
	booking, err := c.bookingPort.GetBookingByID(ctx, req.BookingID)
	if err != nil {
		log.Error(ctx, "Get booking by ID failed: ", err)
		return nil, err
	}

	// if booking.TouristID != userID {
	// 	log.Error(ctx, "user ", userID, " does not have permission to check in booking ", req.BookingID)
	// 	return &response.CheckInResponse{
	// 		Success: false,
	// 		Message: "You do not have permission to check in this booking",
	// 	}, nil
	// }

	if booking.Status != constant.APPROVED {
		log.Error(ctx, "booking status is not approved: ", booking.Status)
		return &response.CheckInResponse{
			Success: false,
			Message: "Booking is not approved for check-in",
		}, nil
	}

	// checkin with inventory service
	// var checkInDate string
	// if req.CheckInDate != nil {
	// 	checkInDate = time.Unix(*req.CheckInDate, 0).Format("2006-01-02")
	// } else {
	// 	checkInDate = time.Now().Format("2006-01-02")
	// }
	// checkInInventory := &request.CheckInInventoryRequest{
	// 	BookingID:   booking.ID,
	// 	GuestID:     userID,
	// 	GuestCount:  &req.GuestCount,
	// 	CheckInDate: checkInDate,
	// }
	// inventoryRes, err := c.inventoryPort.CheckInBooking(ctx, checkInInventory)
	// if err != nil {
	// 	log.Error(ctx, "CheckInBooking with inventory service failed: ", err)
	// 	return nil, err
	// }

	tx := c.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollback := c.dbTransactionUseCase.Rollback(tx); errRollback != nil {
			log.Error(ctx, "Rollback transaction failed: ", errRollback)
		}
	}()
	booking.Status = constant.CHECKED_IN
	_, err = c.bookingPort.UpdateBooking(ctx, tx, booking)
	if err != nil {
		return nil, err
	}

	errCommit := c.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		return nil, errCommit
	}

	// return inventoryRes, nil
	log.Info(ctx, "Check-in booking successful for booking ID: ", req.BookingID)
	return nil, nil
}

func NewCheckInBookingUseCase(
	bookingPort port.IBookingPort,
	cachePort port.ICachePort,
	dbTransactionUseCase IDatabaseTransactionUseCase,
	inventoryPort port.IInventoryPort,
) ICheckInBookingUseCase {
	return &CheckInBookingUseCase{
		bookingPort:          bookingPort,
		cachePort:            cachePort,
		dbTransactionUseCase: dbTransactionUseCase,
		inventoryPort:        inventoryPort,
	}
}
