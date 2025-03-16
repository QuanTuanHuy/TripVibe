using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Domain.Service;

public interface IPromotionTypeService
{
    Task<PromotionTypeEntity> CreatePromotionTypeAsync(PromotionTypeEntity promotionType);
}