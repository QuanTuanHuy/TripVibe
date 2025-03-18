using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.UseCase;

namespace PromotionService.Core.Service.Impl;

public class PromotionTypeService : IPromotionTypeService
{
    private readonly ICreatePromotionTypeUseCase createPromotionTypeUseCase;
    private readonly IGetPromotionTypeUseCase getPromotionTypeUseCase;
    private readonly IUpdatePromotionTypeUseCase updatePromotionTypeUseCase;
    
    public PromotionTypeService(ICreatePromotionTypeUseCase createPromotionTypeUseCase,
        IGetPromotionTypeUseCase getPromotionTypeUseCase,
        IUpdatePromotionTypeUseCase updatePromotionTypeUseCase)
    {
        this.createPromotionTypeUseCase = createPromotionTypeUseCase;
        this.getPromotionTypeUseCase = getPromotionTypeUseCase;
        this.updatePromotionTypeUseCase = updatePromotionTypeUseCase;
    }
    
    public async Task<PromotionTypeEntity> CreatePromotionTypeAsync(PromotionTypeEntity promotionType)
    {
        return await createPromotionTypeUseCase.CreatePromotionAsync(promotionType);
    }

    public async Task<(List<PromotionTypeEntity>, int)> GetPromotionTypesAsync(PromotionTypeParams queryParams)
    {
        return await getPromotionTypeUseCase.GetPromotionTypesAsync(queryParams);
    }

    public async Task<PromotionTypeEntity> UpdatePromotionTypeAsync(long id, UpdatePromotionTypeDto req)
    {
        return await updatePromotionTypeUseCase.UpdatePromotionTypeAsync(id, req);
    }
}