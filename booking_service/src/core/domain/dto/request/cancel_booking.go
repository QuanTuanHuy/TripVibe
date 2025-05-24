package request

type CancelBookingRequest struct {
	BookingID int64 `json:"bookingId"`
	UserID    int64 `json:"userId"`
}
