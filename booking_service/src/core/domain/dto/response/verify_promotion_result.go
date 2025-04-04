package response

type VerifyPromotionResponse struct {
	IsValid    bool                     `json:"isValid"`
	Promotions []*VerifyPromotionResult `json:"promotions"`
}

type VerifyPromotionResult struct {
	PromotionID int64  `json:"promotionId"`
	IsValid     bool   `json:"isValid"`
	Message     string `json:"message"`
}
