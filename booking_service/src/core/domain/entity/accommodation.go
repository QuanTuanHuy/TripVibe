package entity

type AccommodationEntity struct {
	BaseEntity
	Name    string        `json:"name"`
	OwnerID int64         `json:"ownerId"`
	Units   []*UnitEntity `json:"units"`
}
