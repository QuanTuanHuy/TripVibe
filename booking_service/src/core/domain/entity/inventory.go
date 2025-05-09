package entity

type InventoryEntity struct {
	BaseEntity
	UnitID          int64
	AccommodationID int64
	Date            int
	Status          string // AVAILABLE, BOOKED, BLOCKED
	BookingID       *int64
	Note            string
}
