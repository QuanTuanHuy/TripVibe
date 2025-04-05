using PromotionService.Core.Domain.Entity;
using PromotionService.Infrastructure.Repository.Model;

namespace PromotionService.Infrastructure.Repository.Mapper;

public class PromotionConditionMapper
{
    public static PromotionConditionEntity ToEntity(PromotionConditionModel promotionCondition)
    {
        return new PromotionConditionEntity
        {
            Id = promotionCondition.Id,
            PromotionId = promotionCondition.PromotionId,
            ConditionId = promotionCondition.ConditionId,
            ConditionValue = promotionCondition.ConditionValue
        };
    }
    
    public static PromotionConditionModel ToModel(PromotionConditionEntity promotionCondition)
    {
        return new PromotionConditionModel
        {
            Id = promotionCondition.Id,
            PromotionId = promotionCondition.PromotionId,
            ConditionId = promotionCondition.ConditionId,
            ConditionValue = promotionCondition.ConditionValue
        };
    }
}