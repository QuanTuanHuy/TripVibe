using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Port;

namespace PromotionService.Core.UseCase.Impl;

public class GetPromotionUseCase : IGetPromotionUseCase
{
    private readonly IPromotionPort _promotionPort;
    private readonly IGetPromotionConditionUseCase _getPromotionConditionUseCase;

    public GetPromotionUseCase(IPromotionPort promotionPort, IGetPromotionConditionUseCase getPromotionConditionUseCase)
    {
        _promotionPort = promotionPort;
        _getPromotionConditionUseCase = getPromotionConditionUseCase;
    }

    public async Task<(List<PromotionEntity>, int)> GetPromotionsAsync(PromotionParams queryParams)
    {
        var (promotions, count) = await _promotionPort.GetPromotionsAsync(queryParams);

        if (!promotions.Any())
        {
            return (promotions, count);
        }

        var promotionIds = promotions.Select(p => p.Id).ToList();

        var promotionConditions = await _getPromotionConditionUseCase.GetConditionsByPromotionIdsAsync(promotionIds);

        var conditionsByPromotionId = promotionConditions
            .GroupBy(pc => pc.PromotionId)
            .ToDictionary(g => g.Key, g => g.ToList());

        foreach (var promotion in promotions)
        {
            if (conditionsByPromotionId.TryGetValue(promotion.Id, out var conditions))
            {
                promotion.Conditions = conditions;
            }
            else
            {
                promotion.Conditions = new List<PromotionConditionEntity>();
            }
        }

        return (promotions, count);
    }

}