using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.UseCase;

public interface IGetPromotionUseCase
{
    Task<(List<PromotionEntity>, int)> GetPromotionsAsync(PromotionParams queryParams);
}