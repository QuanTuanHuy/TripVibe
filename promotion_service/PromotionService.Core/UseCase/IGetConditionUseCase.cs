using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.UseCase;

public interface IGetConditionUseCase 
{
    Task<List<ConditionEntity>> GetConditionsByIdsAsync(List<long> ids);
}