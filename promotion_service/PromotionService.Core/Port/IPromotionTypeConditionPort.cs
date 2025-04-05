using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Port
{
    public interface IPromotionTypeConditionPort
    {
        Task<List<PromotionTypeConditionEntity>> CreatePromotionTypeConditionsAsync(List<PromotionTypeConditionEntity> promotionTypeConditions);
        Task<List<PromotionTypeConditionEntity>> GetPromotionTypeConditionsByPromotionTypeIdAsync(long promotionTypeId);
    }
}