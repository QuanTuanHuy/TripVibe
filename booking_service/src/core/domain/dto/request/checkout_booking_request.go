package request

type CheckOutBookingRequest struct {
	BookingID    int64  `json:"bookingId"`
	CheckoutDate *int64 `json:"checkoutDate,omitempty"`
}

type CheckOutInventoryRequest struct {
	BookingID    int64   `json:"bookingId"`
	CheckoutDate *string `json:"checkOutDate"`
}
