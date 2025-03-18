using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.UseCase;

namespace PromotionService.Core.Service.Impl;

public class PromotionService : IPromotionService
{
    private readonly ICreatePromotionUseCase createPromotionUseCase;
    
    public PromotionService(ICreatePromotionUseCase createPromotionUseCase)
    {
        this.createPromotionUseCase = createPromotionUseCase;
    }
    
    public async Task<PromotionEntity> CreatePromotionAsync(CreatePromotionDto promotion)
    {
        return await createPromotionUseCase.CreatePromotionAsync(promotion);
    }
}