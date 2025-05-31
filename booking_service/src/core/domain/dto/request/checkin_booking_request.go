package request

type CheckInBookingRequest struct {
	CheckInDate *int64 `json:"checkInDate,omitempty"`
	BookingID   int64  `json:"bookingId"`
	GuestCount  int    `json:"guestCount"`
}

type CheckInInventoryRequest struct {
	BookingID   int64  `json:"bookingId"`
	CheckInDate string `json:"checkInDate"`
	GuestID     int64  `json:"guestId"`
	GuestCount  *int   `json:"guestCount,omitempty"`
}
