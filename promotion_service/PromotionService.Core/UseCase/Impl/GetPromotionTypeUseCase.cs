using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Port;

namespace PromotionService.Core.UseCase.Impl;

public class GetPromotionTypeUseCase : IGetPromotionTypeUseCase
{
    private readonly IPromotionTypePort _promotionTypePort;
    
    public GetPromotionTypeUseCase(IPromotionTypePort promotionTypePort)
    {
        _promotionTypePort = promotionTypePort;
    }
    
    public async Task<(List<PromotionTypeEntity>, int)> GetPromotionTypesAsync(PromotionTypeParams queryParams)
    {
        return await _promotionTypePort.GetPromotionTypesWithCountAsync(queryParams);
    }
}