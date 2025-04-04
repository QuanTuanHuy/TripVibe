package request

type VerifyPromotionRequest struct {
	AccommodationID int64   `json:"accommodationId"`
	PromotionIDs    []int64 `json:"promotionIds"`
}
