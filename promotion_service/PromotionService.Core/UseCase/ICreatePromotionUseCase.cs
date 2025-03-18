using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.UseCase;

public interface ICreatePromotionUseCase
{
    Task<PromotionEntity> CreatePromotionAsync(CreatePromotionDto req);
}