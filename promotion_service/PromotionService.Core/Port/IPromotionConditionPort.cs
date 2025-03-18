using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Port;

public interface IPromotionConditionPort
{
    Task<List<PromotionConditionEntity>> CreatePromotionConditionsAsync(List<PromotionConditionEntity> promotionConditions);
}