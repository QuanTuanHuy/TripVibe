using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.UseCase;

public interface IGetPromotionTypeUseCase
{
    Task<(List<PromotionTypeEntity>, int)> GetPromotionTypesAsync(PromotionTypeParams queryParams);
}