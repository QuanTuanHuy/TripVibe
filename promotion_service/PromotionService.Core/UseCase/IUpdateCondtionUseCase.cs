using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.UseCase
{
    public interface IUpdateConditionUseCase
    {
        Task<ConditionEntity> UpdateConditionAsync(long id, UpdateConditionDto req);
    }
}