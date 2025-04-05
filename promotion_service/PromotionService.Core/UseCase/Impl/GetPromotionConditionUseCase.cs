using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Port;

namespace PromotionService.Core.UseCase.Impl;

public class GetPromotionConditionUseCase : IGetPromotionConditionUseCase
{
    private readonly IPromotionConditionPort _promotionConditionPort;
    private readonly IGetConditionUseCase _getConditionUseCase;

    public GetPromotionConditionUseCase(IPromotionConditionPort promotionConditionPort,
        IGetConditionUseCase getConditionUseCase)
    {
        _promotionConditionPort = promotionConditionPort;
        _getConditionUseCase = getConditionUseCase;
    }

    public async Task<List<PromotionConditionEntity>> GetConditionsByPromotionIdAsync(long promotionId)
    {
        var result = await _promotionConditionPort.GetPromotionConditionsByPromotionIdAsync(promotionId);

        var conditionIds = new HashSet<long>(result.Select(x => x.ConditionId)).ToList();
        var conditionMap = (await _getConditionUseCase.GetConditionsByIdsAsync(conditionIds))
            .ToDictionary(x => x.Id);

        foreach (var promotionCondition in result)
        {
            if (conditionMap.TryGetValue(promotionCondition.ConditionId, out var condition))
            {
                promotionCondition.Condition = condition;
            }
        }

        return result;
    }

    public async Task<List<PromotionConditionEntity>> GetConditionsByPromotionIdsAsync(List<long> promotionIds)
    {
        var result = await _promotionConditionPort.GetPromotionConditionsByPromotionIdsAsync(promotionIds);

        var conditionIds = new HashSet<long>(result.Select(x => x.ConditionId)).ToList();
        var conditionMap = (await _getConditionUseCase.GetConditionsByIdsAsync(conditionIds))
            .ToDictionary(x => x.Id);

        foreach (var promotionCondition in result)
        {
            if (conditionMap.TryGetValue(promotionCondition.ConditionId, out var condition))
            {
                promotionCondition.Condition = condition;
            }
        }

        return result;
    }
}