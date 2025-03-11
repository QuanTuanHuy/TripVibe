package entity

type BookingUnitEntity struct {
	BaseEntity
	BookingID int64  `json:"bookingId"`
	UnitID    int64  `json:"unitId"`
	FullName  string `json:"fullName"`
	Email     string `json:"email"`
	Amount    int64  `json:"amount"`
}
