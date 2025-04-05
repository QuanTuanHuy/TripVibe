using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Dto.Response;
using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Service;

public interface IPromotionService
{
    Task<PromotionEntity> CreatePromotionAsync(CreatePromotionDto promotion);
    Task<(List<PromotionEntity>, int)> GetPromotionsAsync(PromotionParams queryParams);
    Task<PromotionEntity> GetDetailPromotionAsync(long id);
    Task StopPromotionAsync(long userId, long id);
    Task<PromotionEntity> UpdatePromotionAsync(long userId, long id, UpdatePromotionDto promotion);
    Task<VerifyPromotionResponse> VerifyPromotion(VerifyPromotionRequest request);
    Task<bool> UpdatePromotionUsage(List<long> promotionIds);
}