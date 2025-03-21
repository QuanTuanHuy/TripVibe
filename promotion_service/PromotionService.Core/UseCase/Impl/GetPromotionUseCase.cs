using PromotionService.Core.Domain.Constant;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Exception;
using PromotionService.Core.Port;

using PromotionService.Kernel.Utils;

namespace PromotionService.Core.UseCase.Impl;

public class GetPromotionUseCase : IGetPromotionUseCase
{
    private readonly IPromotionPort _promotionPort;
    private readonly IGetPromotionConditionUseCase _getPromotionConditionUseCase;
    private readonly IGetPromotionUnitUseCase _getPromotionUnitUseCase;
    private readonly ICachePort _cachePort;

    public GetPromotionUseCase(
        IPromotionPort promotionPort, 
        IGetPromotionConditionUseCase getPromotionConditionUseCase,
        IGetPromotionUnitUseCase getPromotionUnitUseCase,
        ICachePort cachePort)
    {
        _promotionPort = promotionPort;
        _getPromotionConditionUseCase = getPromotionConditionUseCase;
        _getPromotionUnitUseCase = getPromotionUnitUseCase;
        _cachePort = cachePort;
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
        // get from cache
        string cacheKey = CacheUtils.BuildCacheKeyGetPromotionById(id);
        var cachedPromotion = await _cachePort.GetFromCacheAsync<PromotionEntity>(cacheKey);
        
        if (cachedPromotion != null)
        {
            return cachedPromotion;
        }
        
        // get from db
        var promotion = await _promotionPort.GetPromotionByIdAsync(id);
        if (promotion == null)
        {
            throw new AppException(Domain.Constant.ErrorCode.PROMOTION_NOT_FOUND);
        }

        promotion.Conditions = await _getPromotionConditionUseCase.GetConditionsByPromotionIdAsync(id);
        promotion.Units = await _getPromotionUnitUseCase.GetPromotionUnitsByPromotionIdAsync(id);

        // set to cache
        await _cachePort.SetToCacheAsync(cacheKey, promotion, CacheConstant.DEFAULT_TTL);
        
        return promotion;
    }

}