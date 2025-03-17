using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Domain.Port;
using PromotionService.Core.Port;

namespace PromotionService.Core.UseCase.Impl;

public class CreatePromotionTypeUseCase : ICreatePromotionTypeUseCase
{
    private readonly IPromotionTypePort promotionTypePort;
    private readonly IDbTransactionPort dbTransactionPort;

    public CreatePromotionTypeUseCase(IPromotionTypePort promotionTypePort, IDbTransactionPort dbTransactionPort)
    {
        this.promotionTypePort = promotionTypePort;
        this.dbTransactionPort = dbTransactionPort;
    }
    
    public async Task<PromotionTypeEntity> CreatePromotionAsync(PromotionTypeEntity promotionType)
    {
        return await dbTransactionPort.ExecuteInTransactionAsync(async () =>
        {
            // validate promotion type name not exist
            var existingPromotionType = await promotionTypePort.GetPromotionTypeByNameAsync(promotionType.Name);
            if (existingPromotionType != null)
            {
                throw new Exception("Promotion type name already exists");
            }
        
            return await promotionTypePort.AddPromotionTypeAsync(promotionType);
        });
    }
}