package event

type BookingCompletedEvent struct {
	BookingID int64 `json:"id"`
}
