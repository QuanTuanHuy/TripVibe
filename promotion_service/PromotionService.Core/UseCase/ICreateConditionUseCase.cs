using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.UseCase;

public interface ICreateConditionUseCase
{
    Task<ConditionEntity> CreateConditionAsync(CreateConditionDto condition);
}