package request

type BookingParams struct {
	BaseParams
	UserID          *int64  `json:"userId" form:"userId"`
	Status          *string `json:"status" form:"status"`
	UnitID          *int64  `json:"unitId" form:"unitId"`
	AccommodationID *int64  `json:"accommodationId" form:"accommodationId"`
	StartTime       *int64  `json:"startTime" form:"startTime"`
	EndTime         *int64  `json:"endTime" form:"endTime"`
	DateType        *string `json:"dateType" form:"dateType"`
}
