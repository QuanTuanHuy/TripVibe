using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.UseCase;

namespace PromotionService.Core.Service.Impl;

public class ConditionService : IConditionService
{
    private readonly ICreateConditionUseCase _createConditionUseCase;

    public ConditionService(ICreateConditionUseCase createConditionUseCase)
    {
        _createConditionUseCase = createConditionUseCase;
    }
    
    public async Task<ConditionEntity> CreateConditionAsync(CreateConditionDto condition)
    {
        return await _createConditionUseCase.CreateConditionAsync(condition);
    }
}