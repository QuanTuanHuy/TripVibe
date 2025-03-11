package entity

type BookingPromotionEntity struct {
	BaseEntity
	BookingID   int64 `json:"bookingId"`
	PromotionID int64 `json:"promotionId"`
	Percentage  int   `json:"percentage"`
}
