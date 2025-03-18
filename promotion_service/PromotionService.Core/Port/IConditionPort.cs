using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Port;

public interface IConditionPort
{
    Task<ConditionEntity> CreateConditionAsync(ConditionEntity condition);
    Task<ConditionEntity> GetConditionByNameAsync(string name);
    Task<List<ConditionEntity>> GetConditionsByIdsAsync(List<long> ids);
}