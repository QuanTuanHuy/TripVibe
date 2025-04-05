using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Dto.Response;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.UseCase;

namespace PromotionService.Core.Service.Impl;

public class PromotionService : IPromotionService
{
    private readonly ICreatePromotionUseCase _createPromotionUseCase;
    private readonly IGetPromotionUseCase _getPromotionUseCase;
    private readonly IUpdatePromotionUseCase _updatePromotionUseCase;
    private readonly IVerifyPromotionUseCase _verifyPromotionUseCase;
    
    public PromotionService(ICreatePromotionUseCase createPromotionUseCase,
        IGetPromotionUseCase getPromotionUseCase,
        IUpdatePromotionUseCase updatePromotionUseCase,
        IVerifyPromotionUseCase verifyPromotionUseCase)
    {
        _createPromotionUseCase = createPromotionUseCase;
        _getPromotionUseCase = getPromotionUseCase;
        _updatePromotionUseCase = updatePromotionUseCase;
        _verifyPromotionUseCase = verifyPromotionUseCase;
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

    public async Task<PromotionEntity> UpdatePromotionAsync(long userId, long id, UpdatePromotionDto req) {
        return await _updatePromotionUseCase.UpdatePromotionAsync(userId, id, req);
    }

    public async Task<VerifyPromotionResponse> VerifyPromotion(VerifyPromotionRequest request) {
        return await _verifyPromotionUseCase.VerifyPromotion(request);
    }

    public async Task<bool> UpdatePromotionUsage(List<long> promotionIds) {
        return await _updatePromotionUseCase.UpdatePromotionUsage(promotionIds);
    }
}