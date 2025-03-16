using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Domain.UseCase;

public interface ICreatePromotionTypeUseCase
{
    Task<PromotionTypeEntity> CreatePromotionAsync(PromotionTypeEntity promotionType);
}