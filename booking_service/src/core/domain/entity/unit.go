package entity

type UnitEntity struct {
	BaseEntity
	Name            string `json:"name"`
	AccommodationID int64  `json:"accommodationId"`
}
