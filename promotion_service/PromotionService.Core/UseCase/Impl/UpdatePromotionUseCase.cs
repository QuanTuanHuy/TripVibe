namespace PromotionService.Core.UseCase.Impl
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Threading.Tasks;
    using Microsoft.Extensions.Logging;
    using PromotionService.Core.Domain.Constant;
    using PromotionService.Core.Domain.Dto.Request;
    using PromotionService.Core.Domain.Entity;
    using PromotionService.Core.Domain.Port;
    using PromotionService.Core.Exception;
    using PromotionService.Core.Port;
    using PromotionService.Kernel.Utils;

    public class UpdatePromotionUseCase : IUpdatePromotionUseCase
    {
        private readonly IPromotionPort _promotionPort;
        private readonly IPromotionUnitPort _promotionUnitPort;
        private readonly IPromotionConditionPort _promotionConditionPort;
        private readonly ICachePort _cachePort;
        private readonly IDbTransactionPort _dbTransactionPort;
        private readonly IGetPromotionUseCase _getPromotionUseCase;
        private readonly IGetConditionUseCase _getConditionUseCase;
        private readonly ILogger<UpdatePromotionUseCase> _logger;

        public UpdatePromotionUseCase(
            IPromotionPort promotionPort,
            ICachePort cachePort,
            IDbTransactionPort dbTransactionPort,
            ILogger<UpdatePromotionUseCase> logger,
            IGetPromotionUseCase getPromotionUseCase,
            IGetConditionUseCase getConditionUseCase,
            IPromotionUnitPort promotionUnitPort,
            IPromotionConditionPort promotionConditionPort)
        {
            _promotionPort = promotionPort;
            _cachePort = cachePort;
            _dbTransactionPort = dbTransactionPort;
            _getPromotionUseCase = getPromotionUseCase;
            _getConditionUseCase = getConditionUseCase;
            _promotionUnitPort = promotionUnitPort;
            _promotionConditionPort = promotionConditionPort;
            _logger = logger;
        }

        public async Task StopPromotionAsync(long userId, long id)
        {
            var promotion = await _promotionPort.GetPromotionByIdAsync(id);
            if (promotion == null)
            {
                _logger.LogError("Promotion not found with ID: {PromotionId}", id);
                throw new AppException(ErrorCode.PROMOTION_NOT_FOUND);
            }

            ValidatePromotionStateForStop(promotion, userId);

            promotion.IsActive = false;
            await _promotionPort.UpdatePromotionAsync(promotion);

            await ClearPromotionCache(id);
        }

        public async Task<PromotionEntity> UpdatePromotionAsync(long userId, long id, UpdatePromotionDto req)
        {
            var promotion = await _getPromotionUseCase.GetDetailPromotionAsync(id);
            if (promotion == null)
            {
                _logger.LogError("Promotion not found with ID: {PromotionId}", id);
                throw new AppException(ErrorCode.PROMOTION_NOT_FOUND);
            }

            if (promotion.CreatedBy != userId)
            {
                _logger.LogError("User {UserId} is not allowed to update promotion {PromotionId}", userId, id);
                throw new AppException(ErrorCode.GENERAL_FORBIDDEN);
            }

            await ValidatePromotionRequest(req);

            promotion = req.ToEntity(promotion);

            await _dbTransactionPort.ExecuteInTransactionAsync(async () =>
            {
                await _promotionPort.UpdatePromotionAsync(promotion);

                await UpdatePromotionConditions(promotion.Id, req.Conditions);

                await UpdatePromotionUnits(promotion.Id, req.Units);
            });


            await ClearPromotionCache(id);

            return promotion;
        }

        public async Task<bool> UpdatePromotionUsage(List<long> promotionIds)
        {
            var promotions = await _promotionPort.GetPromotionsByIds(promotionIds);
            if (promotions == null || promotions.Count == 0)
            {
                _logger.LogError("No promotions found for IDs: {PromotionIds}", string.Join(", ", promotionIds));
                return false;
            }

            foreach (var promotion in promotions)
            {
                promotion.UsageCount += 1;
                await _promotionPort.UpdatePromotionAsync(promotion);
            }

            return true;
        }

        private void ValidatePromotionStateForStop(PromotionEntity promotion, long userId)
        {
            if (promotion.IsActive == false)
            {
                _logger.LogWarning("Promotion {PromotionId} is already stopped", promotion.Id);
                return;
            }

            if (promotion.CreatedBy != userId)
            {
                _logger.LogError("User {UserId} is not allowed to stop promotion {PromotionId}", userId, promotion.Id);
                throw new AppException(ErrorCode.GENERAL_FORBIDDEN);
            }
        }

        private async Task ValidatePromotionRequest(UpdatePromotionDto req)
        {
            if (req.StartDate > req.EndDate)
            {
                _logger.LogError("Invalid date range: Start date {StartDate} must be before end date {EndDate}",
                    DateTimeOffset.FromUnixTimeMilliseconds(req.StartDate).ToString(),
                    DateTimeOffset.FromUnixTimeMilliseconds(req.EndDate).ToString());
                throw new AppException(ErrorCode.INVALID_TIME_RANGE);
            }

            if (req.Conditions?.Any() == true)
            {
                var conditionIds = req.Conditions.Select(c => c.ConditionId).ToList();
                var conditions = await _getConditionUseCase.GetConditionsByIdsAsync(conditionIds);

                if (conditions.Count != conditionIds.Count)
                {
                    var missingIds = conditionIds.Except(conditions.Select(c => c.Id)).ToList();
                    _logger.LogError("Conditions not found: {MissingIds}", string.Join(", ", missingIds));
                    throw new AppException(ErrorCode.PROMOTION_CONDITION_NOT_FOUND);
                }
            }
        }

        private async Task UpdatePromotionConditions(long promotionId, List<CreatePromotionConditionDto> conditions)
        {
            await _promotionConditionPort.DeletePromotionConditionsByPromotionIdAsync(promotionId);

            if (conditions?.Any() == true)
            {
                var promotionConditions = conditions.Select(c => c.ToEntity(promotionId)).ToList();
                await _promotionConditionPort.CreatePromotionConditionsAsync(promotionConditions);
            }
        }

        private async Task UpdatePromotionUnits(long promotionId, List<CreatePromotionUnitDto> units)
        {
            await _promotionUnitPort.DeletePromotionUnitsByPromotionIdAsync(promotionId);

            if (units?.Any() == true)
            {
                var promotionUnits = units.Select(unit => unit.ToEntity(promotionId)).ToList();
                await _promotionUnitPort.CreatePromotionUnitsAsync(promotionUnits);
            }
        }

        private async Task ClearPromotionCache(long promotionId)
        {
            var cacheKey = CacheUtils.BuildCacheKeyGetPromotionById(promotionId);
            await _cachePort.DeleteFromCacheAsync(cacheKey);
        }
    }
}