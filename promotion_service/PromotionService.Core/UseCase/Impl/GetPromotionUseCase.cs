using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Port;

namespace PromotionService.Core.UseCase.Impl;

public class GetPromotionUseCase : IGetPromotionUseCase
{
    private readonly IPromotionPort _promotionPort;
    private readonly IGetPromotionConditionUseCase _getPromotionConditionUseCase;
    private readonly IGetPromotionUnitUseCase _getPromotionUnitUseCase;

    public GetPromotionUseCase(IPromotionPort promotionPort, IGetPromotionConditionUseCase getPromotionConditionUseCase,
        IGetPromotionUnitUseCase getPromotionUnitUseCase)
    {
        _promotionPort = promotionPort;
        _getPromotionConditionUseCase = getPromotionConditionUseCase;
        _getPromotionUnitUseCase = getPromotionUnitUseCase;
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

        var promotionUnits = await _getPromotionUnitUseCase.GetPromotionUnitsByPromotionIdsAsync(promotionIds);
        var unitsByPromotionId = promotionUnits
            .GroupBy(pu => pu.PromotionId)
            .ToDictionary(g => g.Key, g => g.ToList());

        foreach (var promotion in promotions)
        {
            if (conditionsByPromotionId.TryGetValue(promotion.Id, out var conditions))
            {
                promotion.Conditions = conditions;
            }

            if (unitsByPromotionId.TryGetValue(promotion.Id, out var units))
            {
                promotion.Units = units;
            }
        }

        return (promotions, count);
    }

    public async Task<PromotionEntity> GetDetailPromotionAsync(long id)
    {
        var promotion = await _promotionPort.GetPromotionByIdAsync(id);
        if (promotion == null)
        {
            throw new Exception("Promotion not found");
        }

        promotion.Conditions = await _getPromotionConditionUseCase.GetConditionsByPromotionIdAsync(id);
        promotion.Units = await _getPromotionUnitUseCase.GetPromotionUnitsByPromotionIdAsync(id);

        return promotion;
    }

}