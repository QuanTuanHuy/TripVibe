using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Domain.Mapper;

public class ConditionMapper
{
    public static ConditionEntity ToEntity(CreateConditionDto req)
    {
        return new ConditionEntity(req.Name, req.Description);
    }
}