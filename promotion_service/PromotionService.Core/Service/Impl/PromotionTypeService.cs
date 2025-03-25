using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.UseCase;

namespace PromotionService.Core.Service.Impl;

public class PromotionTypeService : IPromotionTypeService
{
    private readonly ICreatePromotionTypeUseCase createPromotionTypeUseCase;
    private readonly IGetPromotionTypeUseCase getPromotionTypeUseCase;
    private readonly IUpdatePromotionTypeUseCase updatePromotionTypeUseCase;
    private readonly IDeletePromotionTypeUseCase deletePromotionTypeUseCase;
    private readonly IAddConditionToPromotionTypeUseCase addConditionToPromotionTypeUseCase;
    
    public PromotionTypeService(ICreatePromotionTypeUseCase createPromotionTypeUseCase,
        IGetPromotionTypeUseCase getPromotionTypeUseCase,
        IUpdatePromotionTypeUseCase updatePromotionTypeUseCase,
        IDeletePromotionTypeUseCase deletePromotionTypeUseCase,
        IAddConditionToPromotionTypeUseCase addConditionToPromotionTypeUseCase)
    {
        this.createPromotionTypeUseCase = createPromotionTypeUseCase;
        this.getPromotionTypeUseCase = getPromotionTypeUseCase;
        this.updatePromotionTypeUseCase = updatePromotionTypeUseCase;
        this.deletePromotionTypeUseCase = deletePromotionTypeUseCase;
        this.addConditionToPromotionTypeUseCase = addConditionToPromotionTypeUseCase;
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

    public async Task DeletePromotionTypeAsync(long id)
    {
        await deletePromotionTypeUseCase.DeletePromotionTypeAsync(id);
    }

    public async Task<List<PromotionTypeConditionEntity>> AddConditionsToPromotionTypeAsync(long promotionTypeId, AddConditionToPromotionTypeDto req) {
        return await addConditionToPromotionTypeUseCase.AddConditionToPromotionTypeAsync(promotionTypeId, req);
    }

    public async Task<PromotionTypeEntity> GetPromotionTypeByIdAsync(long id) {
        return await getPromotionTypeUseCase.GetPromotionTypeByIdAsync(id);
    }
}