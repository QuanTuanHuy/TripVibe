package request

type BookingStatisticsRequest struct {
	StartDate       *int64  `json:"startDate" form:"startDate"`
	EndDate         *int64  `json:"endDate" form:"endDate"`
	DateType        *string `json:"dateType" form:"dateType"`
	AccommodationID int64   `json:"accommodationId" form:"accommodationId"`
	UnitID          *int64  `json:"unitId" form:"unitId"`
}
