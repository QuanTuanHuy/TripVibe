using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.UseCase;

namespace PromotionService.Core.Service.Impl;

public class PromotionService : IPromotionService
{
    private readonly ICreatePromotionUseCase _createPromotionUseCase;
    private readonly IGetPromotionUseCase _getPromotionUseCase;
    private readonly IUpdatePromotionUseCase _updatePromotionUseCase;
    
    public PromotionService(ICreatePromotionUseCase createPromotionUseCase, IGetPromotionUseCase getPromotionUseCase,
        IUpdatePromotionUseCase updatePromotionUseCase)
    {
        _createPromotionUseCase = createPromotionUseCase;
        _getPromotionUseCase = getPromotionUseCase;
        _updatePromotionUseCase = updatePromotionUseCase;
    }

    public async Task<PromotionEntity> CreatePromotionAsync(CreatePromotionDto promotion)
    {
        return await _createPromotionUseCase.CreatePromotionAsync(promotion);
    }

    public async Task<(List<PromotionEntity>, int)> GetPromotionsAsync(PromotionParams queryParams)
    {
        return await _getPromotionUseCase.GetPromotionsAsync(queryParams);
    }

    public async Task<PromotionEntity> GetDetailPromotionAsync(long id)
    {
        return await _getPromotionUseCase.GetDetailPromotionAsync(id);
    }

    public async Task StopPromotionAsync(long userId, long id)
    {
        await _updatePromotionUseCase.StopPromotionAsync(userId, id);
    }
}