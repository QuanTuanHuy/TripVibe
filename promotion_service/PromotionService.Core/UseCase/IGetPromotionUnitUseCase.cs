namespace PromotionService.Core.UseCase
{
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using PromotionService.Core.Domain.Entity;

    public interface IGetPromotionUnitUseCase
    {
        Task<List<PromotionUnitEntity>> GetPromotionUnitsByPromotionIdAsync(long promotionId);
        Task<List<PromotionUnitEntity>> GetPromotionUnitsByPromotionIdsAsync(List<long> promotionIds);
    }
}