using Microsoft.Extensions.Logging;
using PromotionService.Core.Domain.Constant;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Domain.Mapper;
using PromotionService.Core.Domain.Port;
using PromotionService.Core.Exception;
using PromotionService.Core.Port;
using PromotionService.Kernel.Utils;

namespace PromotionService.Core.UseCase.Impl;

public class UpdatePromotionTypeUseCase : IUpdatePromotionTypeUseCase
{
    private readonly IPromotionTypePort _promotionTypePort;
    private readonly IDbTransactionPort _dbTransactionPort;
    private readonly ICachePort _cachePort;
    private readonly ILogger<UpdatePromotionTypeUseCase> _logger;

    public UpdatePromotionTypeUseCase(IPromotionTypePort promotionTypePort, IDbTransactionPort dbTransactionPort,
        ICachePort cachePort, ILogger<UpdatePromotionTypeUseCase> logger)
    {
        _promotionTypePort = promotionTypePort;
        _dbTransactionPort = dbTransactionPort;
        _logger = logger;
        _cachePort = cachePort;
    }

    public async Task<PromotionTypeEntity> UpdatePromotionTypeAsync(long id, UpdatePromotionTypeDto req)
    {
        var promotionType = await _promotionTypePort.GetPromotionTypeByIdAsync(id);
        if (promotionType == null)
        {
            _logger.LogError("Promotion type not found, ID: {Id}", id);
            throw new AppException(ErrorCode.PROMOTION_TYPE_NOT_FOUND);
        }

        var exitedName = await _promotionTypePort.GetPromotionTypeByNameAsync(req.Name);
        if (exitedName != null && exitedName.Id != id)
        {
            _logger.LogError("Promotion type name already exists, Name: {Name}", req.Name);
            throw new AppException(ErrorCode.PROMOTION_TYPE_NAME_EXIST);
        }

        promotionType.Name = req.Name;
        promotionType.Description = req.Description;

        return await _dbTransactionPort.ExecuteInTransactionAsync(async () => 
        {
            var updatedData = await _promotionTypePort.UpdatePromotionTypeAsync(id, promotionType);
            await _cachePort.DeleteFromCacheAsync(CacheUtils.BuildCacheKeyGetPromotionTypeById(id));
            return updatedData;
        });
    }
}