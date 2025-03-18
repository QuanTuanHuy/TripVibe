using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Domain.Mapper;
using PromotionService.Core.Domain.Port;
using PromotionService.Core.Port;

namespace PromotionService.Core.UseCase.Impl;

public class UpdatePromotionTypeUseCase : IUpdatePromotionTypeUseCase
{
    private readonly IPromotionTypePort _promotionTypePort;
    private readonly IDbTransactionPort _dbTransactionPort;

    public UpdatePromotionTypeUseCase(IPromotionTypePort promotionTypePort, IDbTransactionPort dbTransactionPort)
    {
        _promotionTypePort = promotionTypePort;
        _dbTransactionPort = dbTransactionPort;
    }
    
    public async Task<PromotionTypeEntity> UpdatePromotionTypeAsync(long id, UpdatePromotionTypeDto req)
    {
        return await _dbTransactionPort.ExecuteInTransactionAsync(async () => 
        {
            return await _promotionTypePort.UpdatePromotionTypeAsync(id, PromotionTypeMapper.ToEntity(req));
        });
    }
}