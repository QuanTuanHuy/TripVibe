package client

import (
	"booking_service/core/domain/constant"
	"booking_service/core/domain/dto/request"
	"booking_service/core/domain/dto/response"
	"booking_service/core/port"
	"context"
	"github.com/golibs-starter/golib/log"
	"time"
)

type PromotionClientAdapter struct {
	apiClient *ApiClient
}

func (p PromotionClientAdapter) UpdatePromotionUsage(ctx context.Context, promotionIDs []int64) error {
	var req struct {
		PromotionIDs []int64 `json:"promotionIds"`
	}
	req.PromotionIDs = promotionIDs

	var result bool
	err := p.apiClient.PutJSON(ctx, "promotion", "/api/public/v1/promotions/update_usage", req, &result)
	if err != nil {
		log.Error(ctx, "UpdatePromotionUsage error ", err)
		return err
	}
	return nil
}

func (p PromotionClientAdapter) VerifyPromotion(ctx context.Context, req *request.VerifyPromotionRequest) (*response.VerifyPromotionResponse, error) {
	var result response.VerifyPromotionResponse

	err := p.apiClient.PostJSON(ctx, constant.PROMOTION_SERVICE, constant.VERIFY_PROMOTION_ENPOINT, req, &result)
	if err != nil {
		log.Error(ctx, "VerifyPromotion error ", err)
		return nil, err
	}
	return &result, nil
}

func NewPromotionClientAdapter() port.IPromotionPort {
	apiClient := NewApiClient(
		WithService(constant.PROMOTION_SERVICE, constant.PROMOTION_SERVICE_URL, 10*time.Second),
		WithServiceRetry(constant.PROMOTION_SERVICE, 3, 500*time.Millisecond),
	)
	return &PromotionClientAdapter{
		apiClient: apiClient,
	}
}
