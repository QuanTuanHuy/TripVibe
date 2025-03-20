namespace PromotionService.Core.UseCase.Impl
{
    using System.Threading.Tasks;
    using Microsoft.Extensions.Logging;
    using PromotionService.Core.Domain.Port;
    using PromotionService.Core.Port;

    public class UpdatePromotionUseCase : IUpdatePromotionUseCase
    {
        private readonly IPromotionPort _promotionPort;
        private readonly IDbTransactionPort _dbTransactionPort;
        private readonly ILogger<UpdatePromotionUseCase> _logger;

        public UpdatePromotionUseCase(IPromotionPort promotionPort,
            IDbTransactionPort dbTransactionPort, ILogger<UpdatePromotionUseCase> logger)
        {
            _promotionPort = promotionPort;
            _dbTransactionPort = dbTransactionPort;
            _logger = logger;
        }

        public async Task StopPromotionAsync(long userId, long id)
        {
            var promotion = await _promotionPort.GetPromotionByIdAsync(id);
            if (promotion.IsActive == false)
            {
                _logger.LogError("Promotion is already stopped");
                return;
            }
            // need to check user is owner of accommodation in promotion

            promotion.IsActive = false;

            await _dbTransactionPort.ExecuteInTransactionAsync(async () =>
            {
                await _promotionPort.UpdatePromotionAsync(promotion);
            });
        }
    }
}