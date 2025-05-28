package response

import "booking_service/core/domain/entity"

type BookingResponse struct {
	ID              int64  `json:"id"`
	TouristID       int64  `json:"touristId"`
	AccommodationID int64  `json:"accommodationId"`
	NumberOfAdult   int    `json:"numberOfAdult"`
	NumberOfChild   int    `json:"numberOfChild"`
	PaymentID       *int64 `json:"paymentId"`
	CurrencyID      int64  `json:"currencyId"`
	Status          string `json:"status"`
	Note            string `json:"note"`
	StayFrom        int64  `json:"stayFrom"`
	StayTo          int64  `json:"stayTo"`
	InvoiceAmount   int64  `json:"invoiceAmount"`
	FinalAmount     int64  `json:"finalAmount"`

	Tourist    *UserResponse               `json:"tourist,omitempty"`
	Units      []*BookingUnitResponse      `json:"units"`
	Promotions []*BookingPromotionResponse `json:"promotions,omitempty"`
}

type GetBookingResponse struct {
	Bookings     []*BookingResponse `json:"data"`
	TotalItems   int64              `json:"totalItems"`
	TotalPages   int64              `json:"totalPages"`
	CurrentPage  int64              `json:"currentPage"`
	PageSize     int64              `json:"pageSize"`
	PreviousPage *int64             `json:"previousPage"`
	NextPage     *int64             `json:"nextPage"`
}

func ToBookingResponse(booking *entity.BookingEntity) *BookingResponse {
	return &BookingResponse{
		ID:              booking.ID,
		Status:          booking.Status,
		TouristID:       booking.TouristID,
		AccommodationID: booking.AccommodationID,
		NumberOfAdult:   booking.NumberOfAdult,
		NumberOfChild:   booking.NumberOfChild,
		PaymentID:       booking.PaymentID,
		CurrencyID:      booking.CurrencyID,
		Note:            booking.Note,
		StayFrom:        booking.StayFrom,
		StayTo:          booking.StayTo,
		InvoiceAmount:   booking.InvoiceAmount,
		FinalAmount:     booking.FinalAmount,
		Tourist:         ToUserResponse(booking.Tourist),
		Units:           ToListBookingUnitResponse(booking.Units),
		Promotions:      ToListBookingPromotionResponse(booking.Promotions),
	}
}

func ToListBookingResponse(bookings []*entity.BookingEntity) []*BookingResponse {
	var responses []*BookingResponse
	for _, booking := range bookings {
		responses = append(responses, ToBookingResponse(booking))
	}
	return responses
}

func ToGetBookingResponse(bookings []*entity.BookingEntity, page, pageSize, totalPage, total int64, prevPage, nextPage *int64) *GetBookingResponse {
	return &GetBookingResponse{
		Bookings:     ToListBookingResponse(bookings),
		TotalItems:   total,
		TotalPages:   totalPage,
		CurrentPage:  page,
		PageSize:     pageSize,
		PreviousPage: prevPage,
		NextPage:     nextPage,
	}

}
