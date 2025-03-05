package entity

type AccommodationEntity struct {
	BaseEntity
	Name  string        `json:"name"`
	Units []*UnitEntity `json:"units"`
}
