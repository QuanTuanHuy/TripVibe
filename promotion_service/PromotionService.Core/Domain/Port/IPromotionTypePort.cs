using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Domain.Port;

public interface IPromotionTypePort
{
    Task<PromotionTypeEntity> AddPromotionTypeAsync(PromotionTypeEntity promotionType);
    Task<PromotionTypeEntity> GetPromotionTypeByNameAsync(string name);
}