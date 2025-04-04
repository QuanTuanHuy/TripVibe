using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Dto.Response;

namespace PromotionService.Core.UseCase
{
    public interface IVerifyPromotionUseCase
    {
        Task<VerifyPromotionResponse> VerifyPromotion(VerifyPromotionRequest request);
    }
}