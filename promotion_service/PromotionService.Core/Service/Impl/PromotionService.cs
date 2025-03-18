using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.UseCase;

namespace PromotionService.Core.Service.Impl;

public class PromotionService : IPromotionService
{
    private readonly ICreatePromotionUseCase _createPromotionUseCase;
    private readonly IGetPromotionUseCase _getPromotionUseCase;
    
    public PromotionService(ICreatePromotionUseCase createPromotionUseCase, IGetPromotionUseCase getPromotionUseCase)
    {
        _createPromotionUseCase = createPromotionUseCase;
        _getPromotionUseCase = getPromotionUseCase;
    }

    public async Task<PromotionEntity> CreatePromotionAsync(CreatePromotionDto promotion)
    {
        return await _createPromotionUseCase.CreatePromotionAsync(promotion);
    }

    public async Task<(List<PromotionEntity>, int)> GetPromotionsAsync(PromotionParams queryParams)
    {
        return await _getPromotionUseCase.GetPromotionsAsync(queryParams);
    }
}