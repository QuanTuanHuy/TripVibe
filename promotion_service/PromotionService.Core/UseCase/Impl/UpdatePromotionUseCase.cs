namespace PromotionService.Core.UseCase.Impl
{
    using System.Threading.Tasks;
    using Microsoft.Extensions.Logging;
    using PromotionService.Core.Domain.Constant;
    using PromotionService.Core.Domain.Port;
    using PromotionService.Core.Exception;
    using PromotionService.Core.Port;
    using PromotionService.Kernel.Utils;

    public class UpdatePromotionUseCase : IUpdatePromotionUseCase
    {
        private readonly IPromotionPort _promotionPort;
        private readonly ICachePort _cachePort;
        private readonly IDbTransactionPort _dbTransactionPort;
        private readonly ILogger<UpdatePromotionUseCase> _logger;

        public UpdatePromotionUseCase(IPromotionPort promotionPort,
            ICachePort cachePort,
            IDbTransactionPort dbTransactionPort, ILogger<UpdatePromotionUseCase> logger)
        {
            _promotionPort = promotionPort;
            _cachePort = cachePort;
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
            if (promotion.CreatedBy != userId)
            {
                _logger.LogError($"User {userId} is not allowed to stop this promotion {id}");
                throw new AppException(ErrorCode.GENERAL_FORBIDDEN);
            }

            promotion.IsActive = false;

            await _dbTransactionPort.ExecuteInTransactionAsync(async () =>
            {
                await _promotionPort.UpdatePromotionAsync(promotion);
            });

            // clear cache
            await _cachePort.DeleteFromCacheAsync(CacheUtils.BuildCacheKeyGetPromotionById(id));
        }
    }
}