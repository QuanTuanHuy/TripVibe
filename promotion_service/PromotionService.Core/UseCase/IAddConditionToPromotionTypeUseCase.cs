using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.UseCase
{
    public interface IAddConditionToPromotionTypeUseCase
    {
        Task<List<PromotionTypeConditionEntity>> AddConditionToPromotionTypeAsync(long promotionTypeId, AddConditionToPromotionTypeDto req);
    }
}