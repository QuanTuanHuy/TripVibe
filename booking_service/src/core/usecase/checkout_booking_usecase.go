package usecase

import (
	"booking_service/core/domain/constant"
	basedto "booking_service/core/domain/dto/kafka/base"
	"booking_service/core/domain/dto/kafka/event"
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/dto/response"
	"booking_service/core/port"
	"booking_service/kernel/utils"
	"context"
	"time"

	"github.com/golibs-starter/golib/web/log"
	"github.com/google/uuid"
)

type ICheckOutBookingUseCase interface {
	CheckOutBooking(ctx context.Context, userID int64, req *request.CheckOutBookingRequest) (*response.CheckOutResponse, error)
}

type CheckOutBookingUseCase struct {
	bookingPort          port.IBookingPort
	inventoryPort        port.IInventoryPort
	kafkaPublisher       port.IKafkaPublisher
	dbTransactionUseCase IDatabaseTransactionUseCase
}

func (c *CheckOutBookingUseCase) CheckOutBooking(ctx context.Context, userID int64, req *request.CheckOutBookingRequest) (*response.CheckOutResponse, error) {
	booking, err := c.bookingPort.GetBookingByID(ctx, req.BookingID)
	if err != nil {
		log.Error(ctx, "Get booking by ID failed: ", err)
		return nil, err
	}
	// if booking.TouristID != userID {
	// 	log.Error(ctx, "user ", userID, " does not have permission to check out booking ", req.BookingID)
	// 	return &response.CheckOutResponse{
	// 		Success: false,
	// 		Message: "You do not have permission to check out this booking",
	// 	}, nil
	// }
	if booking.Status != constant.APPROVED {
		log.Warn(ctx, "Booking status is not approved: ", booking.Status)
	}

	generalFailedResponse := &response.CheckOutResponse{
		Success: false,
		Message: "Failed to check out booking",
	}

	var checkOutDate string
	if req.CheckoutDate != nil {
		checkOutDate = utils.EpochSecondToDate(*req.CheckoutDate)
	} else {
		checkOutDate = utils.EpochSecondToDate(time.Now().Unix())
	}
	log.Info(ctx, "Check out date: %s", checkOutDate)
	// comment for testing purpose
	// inventoryReq := &request.CheckOutInventoryRequest{
	// 	BookingID:    booking.ID,
	// 	CheckoutDate: &checkOutDate,
	// }

	// inventoryRes, err := c.inventoryPort.CheckOutBooking(ctx, inventoryReq)
	// if err != nil {
	// 	log.Error(ctx, "Check out booking failed: ", err)
	// 	return generalFailedResponse, err
	// }

	// if !inventoryRes.Success {
	// 	log.Error(ctx, "Inventory check out failed: ", inventoryRes.Message)
	// 	return inventoryRes, nil
	// }

	log.Info(ctx, "Check out booking successful for booking ID: ", req.BookingID)
	booking.Status = constant.COMPLETED

	tx := c.dbTransactionUseCase.StartTransaction()
	defer func() {
		if errRollback := c.dbTransactionUseCase.Rollback(tx); errRollback != nil {
			log.Error(ctx, "Rollback transaction failed: ", errRollback)
		}
	}()

	_, err = c.bookingPort.UpdateBooking(ctx, tx, booking)
	if err != nil {
		log.Error(ctx, "Update booking status to completed failed: ", err)
		return generalFailedResponse, err
	}

	errCommit := c.dbTransactionUseCase.Commit(tx)
	if errCommit != nil {
		log.Error(ctx, "Commit create booking failed, : ", errCommit)
		return generalFailedResponse, errCommit
	}

	go func() {
		event := &basedto.DomainEventDto{
			EventID:   uuid.New().String(),
			EventType: constant.BOOKING_COMPLETED,
			Timestamp: time.Now().Unix(),
			Data: &event.BookingCompletedEvent{
				BookingID: booking.ID,
			},
		}

		err := c.kafkaPublisher.PushMessage(event, constant.BOOKING_EVENT)
		if err != nil {
			log.Error(ctx, "Push message to Kafka failed: ", err)
		} else {
			log.Info(ctx, "Push message to Kafka successful for booking ID: ", booking.ID)
		}
	}()

	// return inventoryRes, nil
	return nil, nil
}

func NewCheckOutBookingUseCase(
	bookingPort port.IBookingPort,
	inventoryPort port.IInventoryPort,
	kafkaPublisher port.IKafkaPublisher,
	dbTransactionUseCase IDatabaseTransactionUseCase,
) ICheckOutBookingUseCase {
	return &CheckOutBookingUseCase{
		bookingPort:          bookingPort,
		inventoryPort:        inventoryPort,
		kafkaPublisher:       kafkaPublisher,
		dbTransactionUseCase: dbTransactionUseCase,
	}
}
