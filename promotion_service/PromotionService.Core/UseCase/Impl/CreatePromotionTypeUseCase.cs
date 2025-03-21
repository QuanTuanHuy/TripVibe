using PromotionService.Core.Domain.Constant;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Domain.Port;
using PromotionService.Core.Exception;
using PromotionService.Core.Port;

namespace PromotionService.Core.UseCase.Impl;

public class CreatePromotionTypeUseCase : ICreatePromotionTypeUseCase
{
    private readonly IPromotionTypePort _promotionTypePort;
    private readonly IDbTransactionPort _dbTransactionPort;

    public CreatePromotionTypeUseCase(IPromotionTypePort promotionTypePort, IDbTransactionPort dbTransactionPort)
    {
        _promotionTypePort = promotionTypePort;
        _dbTransactionPort = dbTransactionPort;
    }
    
    public async Task<PromotionTypeEntity> CreatePromotionAsync(PromotionTypeEntity promotionType)
    {
        // validate promotion type name not exist
        var existingPromotionType = await _promotionTypePort.GetPromotionTypeByNameAsync(promotionType.Name);
        if (existingPromotionType != null)
        {
            throw new AppException(ErrorCode.PROMOTION_TYPE_NAME_EXIST);
        }
        
        return await _dbTransactionPort.ExecuteInTransactionAsync(async () =>
        {
            return await _promotionTypePort.AddPromotionTypeAsync(promotionType);
        });
    }
}