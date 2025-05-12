package request

type CreateQuickBookingDto struct {
	Name             string  `json:"name" binding:"required"`
	AccommodationID  int64   `json:"accommodationId" binding:"required"`
	NumberOfAdult    int     `json:"numberOfAdult" binding:"required,min=1"`
	NumberOfChild    int     `json:"numberOfChild"`
	PreferredUnitIDs []int64 `json:"preferredUnitIds" binding:"required,min=1"`
}
