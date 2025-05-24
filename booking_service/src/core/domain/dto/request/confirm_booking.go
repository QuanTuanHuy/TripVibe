package request

type ConfirmBookingRequest struct {
	BookingID int64  `json:"bookingId"`
	LockID    string `json:"lockId"`
}
