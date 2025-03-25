using Microsoft.Extensions.Logging;
using PromotionService.Core.Domain.Constant;
using PromotionService.Core.Domain.Port;
using PromotionService.Core.Exception;
using PromotionService.Core.Port;
using PromotionService.Kernel.Utils;

namespace PromotionService.Core.UseCase.Impl
{
    public class DeletePromotionTypeUseCase : IDeletePromotionTypeUseCase
    {
        private readonly IPromotionTypePort _promotionTypePort;
        private readonly IPromotionPort _promotionPort;
        private readonly IPromotionUnitPort _promotionUnitPort;
        private readonly IPromotionConditionPort _promotionConditionPort;
        private readonly IDbTransactionPort _dbTransactionPort;
        private readonly ICachePort _cachePort;
        private readonly ILogger<DeletePromotionTypeUseCase> _logger;

        public DeletePromotionTypeUseCase(IPromotionTypePort promotionTypePort, IDbTransactionPort dbTransactionPort,
        IPromotionPort promotionPort, ICachePort cachePort, IPromotionUnitPort promotionUnitPort,
            IPromotionConditionPort promotionConditionPort, ILogger<DeletePromotionTypeUseCase> logger)
        {
            _promotionTypePort = promotionTypePort;
            _promotionPort = promotionPort;
            _promotionUnitPort = promotionUnitPort;
            _promotionConditionPort = promotionConditionPort;
            _dbTransactionPort = dbTransactionPort;
            _logger = logger;
            _cachePort = cachePort;
        }
        public async Task DeletePromotionTypeAsync(long id)
        {
            var promotionType = await _promotionTypePort.GetPromotionTypeByIdAsync(id);
            if (promotionType == null)
            {
                _logger.LogError("Promotion type not found, ID: {Id}", id);
                throw new AppException(ErrorCode.PROMOTION_TYPE_NOT_FOUND);
            }

            var promotions = await _promotionPort.GetPromotionsByTypeIdAsync(id);
            var promotionIds = promotions.Any() ? promotions.Select(p => p.Id).ToList() : new List<long>();

            await _dbTransactionPort.ExecuteInTransactionAsync(async () =>
            {
                if (promotionIds.Count > 0)
                {
                    var tasks = new List<Task>
                    {
                        _promotionConditionPort.DeletePromotionConditionsByPromotionIdsAsync(promotionIds),
                        _promotionUnitPort.DeletePromotionUnitsByPromotionIdsAsync(promotionIds)
                    };
                    await Task.WhenAll(tasks);
                    await _promotionPort.DeletePromotionsByTypeIdAsync(id);
                }

                await _promotionTypePort.DeletePromotionTypeAsync(id);
            });

            if (promotions.Any())
            {
                var cacheKeys = promotions.Select(p => CacheUtils.BuildCacheKeyGetPromotionById(p.Id)).ToList();
                var cacheTasks = cacheKeys.Select(key => _cachePort.DeleteFromCacheAsync(key));
                await Task.WhenAll(cacheTasks);
            }
        }


    }
}