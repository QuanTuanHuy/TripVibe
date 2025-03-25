using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Port
{
    public interface IPromotionUnitPort
    {
        Task CreatePromotionUnitsAsync(List<PromotionUnitEntity> promotionUnits);
        Task<List<PromotionUnitEntity>> GetPromotionUnitsByPromotionIdAsync(long promotionId);
        Task<List<PromotionUnitEntity>> GetPromotionUnitsByPromotionIdsAsync(List<long> promotionIds);
        Task DeletePromotionUnitsByPromotionIdsAsync(List<long> promotionIds);
        Task DeletePromotionUnitsByPromotionIdAsync(long promotionId);
    }
}