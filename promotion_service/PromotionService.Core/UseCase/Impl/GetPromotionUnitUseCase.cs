namespace PromotionService.Core.UseCase.Impl
{
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using PromotionService.Core.Domain.Entity;
    using PromotionService.Core.Port;

    public class GetPromotionUnitUseCase : IGetPromotionUnitUseCase
    {
        private readonly IPromotionUnitPort _promotionUnitPort;

        public GetPromotionUnitUseCase(IPromotionUnitPort promotionUnitPort)
        {
            _promotionUnitPort = promotionUnitPort;
        }

        public async Task<List<PromotionUnitEntity>> GetPromotionUnitsByPromotionIdAsync(long promotionId)
        {
            return await _promotionUnitPort.GetPromotionUnitsByPromotionIdAsync(promotionId);
        }

        public async Task<List<PromotionUnitEntity>> GetPromotionUnitsByPromotionIdsAsync(List<long> promotionIds)
        {
            return await _promotionUnitPort.GetPromotionUnitsByPromotionIdsAsync(promotionIds);
        }
    }

}