package request

type BookingParams struct {
	BaseParams
	UserID          *int64
	Status          *string
	AccommodationID *int64
}
