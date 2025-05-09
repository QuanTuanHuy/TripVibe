package mapper

import (
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/entity"
)

func ToBookingEntity(req *request.CreateBookingDto) *entity.BookingEntity {
	return &entity.BookingEntity{
		CurrencyID:      req.CurrencyID,
		TouristID:       req.Tourist.TouristID,
		AccommodationID: req.AccommodationID,
		NumberOfAdult:   req.NumberOfAdult,
		NumberOfChild:   req.NumberOfChild,
		Note:            req.Note,
		StayFrom:        req.StayFrom,
		StayTo:          req.StayTo,
		InvoiceAmount:   req.InvoiceAmount,
		FinalAmount:     req.FinalInvoiceAmount,
	}
}

func ToBookingUnitEntity(req *request.BookingUnit, bookingID int64) *entity.BookingUnitEntity {
	return &entity.BookingUnitEntity{
		BookingID: bookingID,
		UnitID:    req.UnitID,
		Quantity:  req.Quantity,
		FullName:  req.FullName,
		Email:     req.Email,
		Amount:    req.Amount,
	}
}

func ToBookingPromotion(req *request.BookingPromotion, bookingID int64) *entity.BookingPromotionEntity {
	return &entity.BookingPromotionEntity{
		BookingID:     bookingID,
		PromotionID:   req.PromotionID,
		PromotionName: req.PromotionName,
		Percentage:    req.DiscountPercentage,
	}
}
