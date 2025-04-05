using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.UseCase;

namespace PromotionService.Core.Service.Impl;

public class ConditionService : IConditionService
{
    private readonly ICreateConditionUseCase _createConditionUseCase;
    private readonly IUpdateConditionUseCase _updateConditionUseCase;

    public ConditionService(
        ICreateConditionUseCase createConditionUseCase,
        IUpdateConditionUseCase updateConditionUseCase)
    {
        _createConditionUseCase = createConditionUseCase;
        _updateConditionUseCase = updateConditionUseCase;
    }

    public async Task<ConditionEntity> CreateConditionAsync(CreateConditionDto condition)
    {
        return await _createConditionUseCase.CreateConditionAsync(condition);
    }

    public async Task<ConditionEntity> UpdateConditionAsync(long id, UpdateConditionDto req)
    {
        return await _updateConditionUseCase.UpdateConditionAsync(id, req);
    }
}