using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.UseCase;

public interface ICreatePromotionTypeUseCase
{
    Task<PromotionTypeEntity> CreatePromotionAsync(PromotionTypeEntity promotionType);
}