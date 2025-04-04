package client

import (
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

func (p PromotionClientAdapter) VerifyPromotion(ctx context.Context, req *request.VerifyPromotionRequest) (*response.VerifyPromotionResponse, error) {
	var result response.VerifyPromotionResponse

	err := p.apiClient.PostJSON(ctx, "promotion", "/api/public/v1/promotions/verify", req, &result)
	if err != nil {
		log.Error(ctx, "VerifyPromotion error ", err)
		return nil, err
	}
	return &result, nil
}

func NewPromotionClientAdapter() port.IPromotionPort {
	apiClient := NewApiClient(
		WithService("promotion", "http://localhost:8087/promotion_service", 10*time.Second),
		WithServiceRetry("promotion", 3, 500*time.Millisecond),
	)
	return &PromotionClientAdapter{
		apiClient: apiClient,
	}
}
