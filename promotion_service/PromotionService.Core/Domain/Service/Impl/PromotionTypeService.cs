using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Domain.UseCase;

namespace PromotionService.Core.Domain.Service.Impl;

public class PromotionTypeService : IPromotionTypeService
{
    private readonly ICreatePromotionTypeUseCase createPromotionTypeUseCase;
    
    public PromotionTypeService(ICreatePromotionTypeUseCase createPromotionTypeUseCase)
    {
        this.createPromotionTypeUseCase = createPromotionTypeUseCase;
    }
    
    public async Task<PromotionTypeEntity> CreatePromotionTypeAsync(PromotionTypeEntity promotionType)
    {
        return await createPromotionTypeUseCase.CreatePromotionAsync(promotionType);
    }
}