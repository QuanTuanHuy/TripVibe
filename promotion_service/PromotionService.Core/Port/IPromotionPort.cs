using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Port;

public interface IPromotionPort
{
    Task<PromotionEntity> CreatePromotionAsync(PromotionEntity promotion);
}