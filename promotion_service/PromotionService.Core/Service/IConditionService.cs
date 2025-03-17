using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Service;

public interface IConditionService
{
    Task<ConditionEntity> CreateConditionAsync(CreateConditionDto condition);
}