package request

type UpdateQuickBookingDto struct {
	Name             *string `json:"name"`
	AccommodationID  *int64  `json:"accommodationId"`
	NumberOfAdult    *int    `json:"numberOfAdult" binding:"min=1"`
	NumberOfChild    *int    `json:"numberOfChild"`
	PreferredUnitIDs []int64 `json:"preferredUnitIds" binding:"min=1"`
}
