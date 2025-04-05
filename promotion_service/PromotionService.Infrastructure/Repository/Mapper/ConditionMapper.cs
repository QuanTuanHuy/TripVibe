using PromotionService.Core.Domain.Entity;
using PromotionService.Infrastructure.Repository.Model;

namespace PromotionService.Infrastructure.Repository.Mapper;

public class ConditionMapper
{
    public static ConditionModel ToModel(ConditionEntity? condition)
    {
        return condition == null ? null : new ConditionModel(condition.Name, condition.Description);
    }

    public static ConditionEntity ToEntity(ConditionModel? condition)
    {
        if (condition == null)
        {
            return null;
        }
        return new ConditionEntity(condition.Name, condition.Description)
        {
            Id = condition.Id
        };
    }
}