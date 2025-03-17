using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.UseCase;

namespace PromotionService.Core.Service.Impl;

public class PromotionTypeService : IPromotionTypeService
{
    private readonly ICreatePromotionTypeUseCase createPromotionTypeUseCase;
    private readonly IGetPromotionTypeUseCase getPromotionTypeUseCase;
    
    public PromotionTypeService(ICreatePromotionTypeUseCase createPromotionTypeUseCase,
        IGetPromotionTypeUseCase getPromotionTypeUseCase)
    {
        this.createPromotionTypeUseCase = createPromotionTypeUseCase;
        this.getPromotionTypeUseCase = getPromotionTypeUseCase;
    }
    
    public async Task<PromotionTypeEntity> CreatePromotionTypeAsync(PromotionTypeEntity promotionType)
    {
        return await createPromotionTypeUseCase.CreatePromotionAsync(promotionType);
    }

    public async Task<(List<PromotionTypeEntity>, int)> GetPromotionTypesAsync(PromotionTypeParams queryParams)
    {
        return await getPromotionTypeUseCase.GetPromotionTypesAsync(queryParams);
    }
}