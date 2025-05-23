package response

type CancelBookingResponse struct {
	Success    bool     `json:"success"`
	BookingID  int64    `json:"bookingId"`
	CanceledAt *string  `json:"canceledAt"`
	Errors     []string `json:"errors"`
}
