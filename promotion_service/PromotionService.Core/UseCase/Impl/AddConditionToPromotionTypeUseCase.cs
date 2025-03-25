using Microsoft.Extensions.Logging;
using PromotionService.Core.Domain.Constant;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Domain.Port;
using PromotionService.Core.Exception;
using PromotionService.Core.Port;
using PromotionService.Kernel.Utils;

namespace PromotionService.Core.UseCase.Impl
{
    public class AddConditionToPromotionTypeUseCase : IAddConditionToPromotionTypeUseCase
    {
        private readonly IPromotionTypeConditionPort _promotionTypeConditionPort;
        private readonly IDbTransactionPort _dbTransactionPort;
        private readonly IGetConditionUseCase _getConditionUseCase;
        private readonly IGetPromotionTypeUseCase _getPromotionTypeUseCase;
        private readonly ILogger<AddConditionToPromotionTypeUseCase> _logger;
        private readonly ICachePort _cachePort;

        public AddConditionToPromotionTypeUseCase(
            IPromotionTypeConditionPort promotionTypeConditionPort,
            IDbTransactionPort dbTransactionPort,
            IGetConditionUseCase getConditionUseCase,
            IGetPromotionTypeUseCase getPromotionTypeUseCase,
            ILogger<AddConditionToPromotionTypeUseCase> logger,
            ICachePort cachePort)
        {
            _promotionTypeConditionPort = promotionTypeConditionPort;
            _dbTransactionPort = dbTransactionPort;
            _getConditionUseCase = getConditionUseCase;
            _getPromotionTypeUseCase = getPromotionTypeUseCase;
            _cachePort = cachePort;
            _logger = logger;
        }

        public async Task<List<PromotionTypeConditionEntity>> AddConditionToPromotionTypeAsync(long promotionTypeId, AddConditionToPromotionTypeDto req)
        {
            var promotionType = await _getPromotionTypeUseCase.GetPromotionTypeByIdAsync(promotionTypeId);
            if (promotionType == null)
            {
                _logger.LogError("Promotion type not found, ID: {Id}", promotionTypeId);
                throw new AppException(ErrorCode.PROMOTION_TYPE_NOT_FOUND);
            }

            var conditionIds = req.Conditions.Select(c => c.ConditionId).ToList();

            var conditions = await _getConditionUseCase.GetConditionsByIdsAsync(conditionIds);
            if (conditions.Count != conditionIds.Count)
            {
                _logger.LogError("Condition not found");
                throw new AppException(ErrorCode.CONDITION_NOT_FOUND);
            }

            var promotionTypeConditions = req.Conditions.Select(c => new PromotionTypeConditionEntity
            {
                PromotionTypeId = promotionTypeId,
                ConditionId = c.ConditionId,
                DefaultValue = c.DefaultValue,
            }).ToList();

            var result = await _dbTransactionPort.ExecuteInTransactionAsync(async () =>
            {
                return await _promotionTypeConditionPort.CreatePromotionTypeConditionsAsync(promotionTypeConditions);
            });

            // clear cache
            await _cachePort.DeleteFromCacheAsync(CacheUtils.BuildCacheKeyGetPromotionTypeById(promotionTypeId));

            return result;
        }
    }
}