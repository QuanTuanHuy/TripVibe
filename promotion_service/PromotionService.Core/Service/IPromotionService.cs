using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Service;

public interface IPromotionService
{
    Task<PromotionEntity> CreatePromotionAsync(CreatePromotionDto promotion);
    Task<(List<PromotionEntity>, int)> GetPromotionsAsync(PromotionParams queryParams);
}