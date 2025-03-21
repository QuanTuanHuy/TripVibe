using PromotionService.Core.Domain.Constant;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Port;
using PromotionService.Kernel.Utils;

namespace PromotionService.Core.UseCase.Impl;

public class GetPromotionTypeUseCase : IGetPromotionTypeUseCase
{
    private readonly IPromotionTypePort _promotionTypePort;
    private readonly ICachePort _cachePort;
    
    public GetPromotionTypeUseCase(IPromotionTypePort promotionTypePort, ICachePort cachePort)
    {
        _promotionTypePort = promotionTypePort;
        _cachePort = cachePort;
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

        await _cachePort.SetToCacheAsync(key, promotionType, CacheConstant.DEFAULT_TTL);

        return promotionType;
    }
}