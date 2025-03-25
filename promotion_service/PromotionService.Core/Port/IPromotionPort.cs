using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Port;

public interface IPromotionPort
{
    Task<PromotionEntity> CreatePromotionAsync(PromotionEntity promotion);
    Task<(List<PromotionEntity>, int)> GetPromotionsAsync(PromotionParams queryParams);
    Task<PromotionEntity> GetPromotionByIdAsync(long id);
    Task UpdatePromotionAsync(PromotionEntity promotion);
    Task DeletePromotionsByTypeIdAsync(long typeId);
    Task<List<PromotionEntity>> GetPromotionsByTypeIdAsync(long typeId);
}