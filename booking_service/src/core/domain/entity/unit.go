package entity

type UnitEntity struct {
	BaseEntity
	Name            string `json:"name"`
	Quantity        int    `json:"quantity"`
	AccommodationID int64  `json:"accommodationId"`
}
