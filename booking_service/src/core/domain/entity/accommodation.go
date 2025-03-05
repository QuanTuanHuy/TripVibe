package entity

type AccommodationEntity struct {
	BaseEntity
	Name  string
	Units []*UnitEntity
}
