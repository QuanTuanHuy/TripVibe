package mapper

import (
	"booking_service/core/domain/entity"
	"booking_service/infrastructure/repository/model"
	"time"
)

func ToBookingEntity(booking *model.BookingModel) *entity.BookingEntity {
	return &entity.BookingEntity{
		BaseEntity: entity.BaseEntity{
			ID:        booking.ID,
			CreatedAt: booking.CreatedAt.Unix(),
			UpdatedAt: booking.UpdatedAt.Unix(),
		},
		TouristID:       booking.TouristID,
		AccommodationID: booking.AccommodationID,
		NumberOfChild:   booking.NumberOfChild,
		NumberOfAdult:   booking.NumberOfAdult,
		PaymentID:       booking.PaymentID,
		CurrencyID:      booking.CurrencyID,
		Status:          booking.Status,
		Note:            booking.Note,
		StayFrom:        booking.StayTo.Unix(),
		StayTo:          booking.StayTo.Unix(),
		InvoiceAmount:   booking.InvoiceAmount,
		FinalAmount:     booking.FinalAmount,
	}
}

func ToBookingModel(booking *entity.BookingEntity) *model.BookingModel {
	return &model.BookingModel{
		BaseModel: model.BaseModel{
			ID: booking.ID,
		},
		TouristID:       booking.TouristID,
		AccommodationID: booking.AccommodationID,
		NumberOfChild:   booking.NumberOfChild,
		NumberOfAdult:   booking.NumberOfAdult,
		PaymentID:       booking.PaymentID,
		CurrencyID:      booking.CurrencyID,
		Status:          booking.Status,
		Note:            booking.Note,
		StayFrom:        time.Unix(booking.StayFrom, 0),
		StayTo:          time.Unix(booking.StayTo, 0),
		InvoiceAmount:   booking.InvoiceAmount,
		FinalAmount:     booking.FinalAmount,
	}
}

func ToListBookingEntity(bookings []*model.BookingModel) []*entity.BookingEntity {
	var bookingEntities []*entity.BookingEntity
	for _, booking := range bookings {
		bookingEntities = append(bookingEntities, ToBookingEntity(booking))
	}
	return bookingEntities
}
