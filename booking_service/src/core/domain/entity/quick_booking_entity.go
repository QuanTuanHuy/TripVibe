package entity

type QuickBookingEntity struct {
	BaseEntity
	UserID           int64  `json:"userId"`
	Name             string `json:"name"`
	AccommodationID  int64  `json:"accommodationId"`
	NumberOfAdult    int    `json:"numberOfAdult"`
	NumberOfChild    int    `json:"numberOfChild"`
	PreferredUnitIDs string `json:"preferredUnitIds"`
	LastUsed         *int64 `json:"lastUsed"`
}
