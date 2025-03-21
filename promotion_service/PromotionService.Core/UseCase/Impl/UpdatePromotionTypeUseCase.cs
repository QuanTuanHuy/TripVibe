using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Domain.Mapper;
using PromotionService.Core.Domain.Port;
using PromotionService.Core.Port;
using PromotionService.Kernel.Utils;

namespace PromotionService.Core.UseCase.Impl;

public class UpdatePromotionTypeUseCase : IUpdatePromotionTypeUseCase
{
    private readonly IPromotionTypePort _promotionTypePort;
    private readonly IDbTransactionPort _dbTransactionPort;
    private readonly ICachePort _cachePort;

    public UpdatePromotionTypeUseCase(IPromotionTypePort promotionTypePort, IDbTransactionPort dbTransactionPort,
        ICachePort cachePort)
    {
        _promotionTypePort = promotionTypePort;
        _dbTransactionPort = dbTransactionPort;
        _cachePort = cachePort;
    }

    public async Task<PromotionTypeEntity> UpdatePromotionTypeAsync(long id, UpdatePromotionTypeDto req)
    {
        return await _dbTransactionPort.ExecuteInTransactionAsync(async () => 
        {
            var updatedData = await _promotionTypePort.UpdatePromotionTypeAsync(id, PromotionTypeMapper.ToEntity(req));
            await _cachePort.DeleteFromCacheAsync(CacheUtils.BuildCacheKeyGetPromotionTypeById(id));
            return updatedData;
        });
    }
}