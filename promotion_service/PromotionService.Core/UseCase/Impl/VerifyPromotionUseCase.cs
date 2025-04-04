using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Dto.Response;
using PromotionService.Kernel.Utils;

namespace PromotionService.Core.UseCase.Impl
{
    public class VerfiyPromotionUseCase : IVerifyPromotionUseCase
    {
        private readonly IGetPromotionUseCase _getPromotionUseCase;

        public VerfiyPromotionUseCase(
            IGetPromotionUseCase getPromotionUseCase)
        {
            _getPromotionUseCase = getPromotionUseCase;
        }

        public async Task<VerifyPromotionResponse> VerifyPromotion(VerifyPromotionRequest request)
        {
            var promotions = await _getPromotionUseCase.GetPromotionsByIds(request.PromotionIds);
            var promotionMap = promotions.ToDictionary(p => p.Id, p => p);

            var verifyResultList = new List<VerifyPromotionResult>();
            foreach (var promotionId in request.PromotionIds)
            {
                if (promotionMap.TryGetValue(promotionId, out var promotion))
                {
                    bool isValid = true;
                    string message = "Promotion is valid";
                    if (promotion.AccommodationId != request.AccommodationId)
                    {
                        isValid = false;
                        message = "Promotion is not valid for this accommodation";
                    }
                    if (DateTimeUtils.ConvertToDateTime(promotion.StartDate) > DateTime.UtcNow
                        || DateTimeUtils.ConvertToDateTime(promotion.EndDate) < DateTime.UtcNow)
                    {
                        isValid = false;
                        message = "Promotion was expired or not started yet";
                    }
                    if (promotion.IsActive == false)
                    {
                        isValid = false;
                        message = "Promotion was stopped";
                    }
                    if (promotion.UsageCount >= promotion.UsageLimit)
                    {
                        isValid = false;
                        message = "Promotion was used up";
                    }
                    var verifyResult = new VerifyPromotionResult
                    {
                        PromotionId = promotionId,
                        IsValid = isValid,
                        Message = message
                    };

                    verifyResultList.Add(verifyResult);
                }
                else
                {
                    var verifyResult = new VerifyPromotionResult
                    {
                        PromotionId = promotionId,
                        IsValid = false,
                        Message = "Promotion not found"
                    };

                    verifyResultList.Add(verifyResult);
                }
            }
            bool isAllValid = verifyResultList.All(r => r.IsValid);
            return new VerifyPromotionResponse
            {
                IsValid = isAllValid,
                Promotions = verifyResultList
            };
        }
    }
}