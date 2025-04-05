package entity

type BookingPromotionEntity struct {
	BaseEntity
	BookingID     int64  `json:"bookingId"`
	PromotionID   int64  `json:"promotionId"`
	PromotionName string `json:"promotionName"`
	Percentage    int    `json:"percentage"`
}
