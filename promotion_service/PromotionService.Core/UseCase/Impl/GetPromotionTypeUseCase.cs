using PromotionService.Core.Domain.Constant;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Exception;
using PromotionService.Core.Port;
using PromotionService.Kernel.Utils;

namespace PromotionService.Core.UseCase.Impl;

public class GetPromotionTypeUseCase : IGetPromotionTypeUseCase
{
    private readonly IPromotionTypePort _promotionTypePort;
    private readonly IPromotionTypeConditionPort _promotionTypeConditionPort;
    private readonly IGetConditionUseCase _getConditionUseCase;
    private readonly ICachePort _cachePort;

    public GetPromotionTypeUseCase(
        IPromotionTypePort promotionTypePort,
        ICachePort cachePort,
        IPromotionTypeConditionPort promotionTypeConditionPort,
        IGetConditionUseCase getConditionUseCase)
    {
        _promotionTypePort = promotionTypePort;
        _cachePort = cachePort;
        _promotionTypeConditionPort = promotionTypeConditionPort;
        _getConditionUseCase = getConditionUseCase;
    }
    public async Task<(List<PromotionTypeEntity>, int)> GetPromotionTypesAsync(PromotionTypeParams queryParams)
    {
        return await _promotionTypePort.GetPromotionTypesWithCountAsync(queryParams);
    }

    public async Task<PromotionTypeEntity> GetPromotionTypeByIdAsync(long id)
    {
        var key = CacheUtils.BuildCacheKeyGetPromotionTypeById(id);
        var cachedPromotionType = await _cachePort.GetFromCacheAsync<PromotionTypeEntity>(key);
        if (cachedPromotionType != null)
        {
            return cachedPromotionType;
        }

        var promotionType = await _promotionTypePort.GetPromotionTypeByIdAsync(id);
        if (promotionType == null)
        {
            throw new AppException(ErrorCode.PROMOTION_TYPE_NOT_FOUND);
        }
        promotionType.Conditions = await GetPromotionTypeConditionByPromotionTypeIdAsync(id);

        await _cachePort.SetToCacheAsync(key, promotionType, CacheConstant.DEFAULT_TTL);

        return promotionType;
    }

    public async Task<List<PromotionTypeConditionEntity>> GetPromotionTypeConditionByPromotionTypeIdAsync(long promotionTypeId)
    {
        var ptConditions = await _promotionTypeConditionPort.GetPromotionTypeConditionsByPromotionTypeIdAsync(promotionTypeId);

        var conditionIds = ptConditions.Select(x => x.ConditionId).ToList();
        var conditions = await _getConditionUseCase.GetConditionsByIdsAsync(conditionIds);
        var conditionMap = conditions.ToDictionary(x => x.Id, x => x);

        foreach (var ptCondition in ptConditions)
        {
            if (conditionMap.TryGetValue(ptCondition.ConditionId, out var condition))
            {
                ptCondition.Condition = condition;
            }
        }

        return ptConditions;
    }
}