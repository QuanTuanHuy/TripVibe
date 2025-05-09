package request

type OneClickBookingDto struct {
	QuickBookingID int64   `json:"quickBookingId" binding:"required"`
	StayFrom       int64   `json:"stayFrom" binding:"required"`
	StayTo         int64   `json:"stayTo" binding:"required"`
	UnitIDs        []int64 `json:"unitIds" binding:"required,min=1"`
	Note           string  `json:"note"`
	PromotionIDs   []int64 `json:"promotionIds"`
}
