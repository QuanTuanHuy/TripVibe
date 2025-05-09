package request

type QuickBookingParams struct {
	Page            *int   `form:"page"`
	PageSize        *int   `form:"pageSize"`
	AccommodationID *int64 `form:"accommodationId"`
}
