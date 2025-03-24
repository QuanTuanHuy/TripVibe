package response

import "booking_service/core/domain/entity"

type BookingInfoResponse struct {
	ID            int64 `json:"bookingId"`
	UserID        int64 `json:"userId"`
	StartDate     int64 `json:"startDate"`
	EndDate       int64 `json:"endDate"`
	NumberOfAdult int   `json:"numberOfAdult"`
	NumberOfChild int   `json:"numberOfChild"`
}

func ToBookingInfoResponse(booking *entity.BookingEntity) *BookingInfoResponse {
	return &BookingInfoResponse{
		ID:            booking.ID,
		UserID:        booking.TouristID,
		StartDate:     booking.StayFrom,
		EndDate:       booking.StayTo,
		NumberOfAdult: booking.NumberOfAdult,
		NumberOfChild: booking.NumberOfChild,
	}
}
