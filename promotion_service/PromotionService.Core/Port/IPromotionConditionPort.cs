using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Port;

public interface IPromotionConditionPort
{
    Task<List<PromotionConditionEntity>> CreatePromotionConditionsAsync(List<PromotionConditionEntity> promotionConditions);
    Task<List<PromotionConditionEntity>> GetPromotionConditionsByPromotionIdAsync(long promotionId);
    Task<List<PromotionConditionEntity>> GetPromotionConditionsByPromotionIdsAsync(List<long> promotionIds);
    Task DeletePromotionConditionsByPromotionIdsAsync(List<long> promotionIds);
    Task DeletePromotionConditionsByPromotionIdAsync(long promotionId);
}