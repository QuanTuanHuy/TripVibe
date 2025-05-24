package response

type ConfirmBookingResponse struct {
	Success     bool     `json:"success"`
	BookingID   int64    `json:"bookingId"`
	ConfirmedAt *string  `json:"confirmedAt"`
	Errors      []string `json:"errors"`
}
