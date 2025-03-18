using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.UseCase;

public interface IGetPromotionConditionUseCase
{
    Task<List<PromotionConditionEntity>> GetConditionsByPromotionIdAsync(long promotionId);
    Task<List<PromotionConditionEntity>> GetConditionsByPromotionIdsAsync(List<long> promotionIds);
}