package request

type BookingParams struct {
	BaseParams
	UserID          *int64
	Status          *string
	UnitID          *int64
	AccommodationID *int64
}
