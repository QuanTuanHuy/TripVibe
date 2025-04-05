using Microsoft.Extensions.Logging;
using PromotionService.Core.Domain.Constant;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Domain.Port;
using PromotionService.Core.Exception;
using PromotionService.Core.Port;

namespace PromotionService.Core.UseCase.Impl;

public class CreatePromotionUseCase : ICreatePromotionUseCase
{
    private readonly IPromotionPort _promotionPort;
    private readonly IPromotionConditionPort _promotionConditionPort;
    private readonly IDbTransactionPort _dbTransactionPort;
    private readonly IPromotionUnitPort _promotionUnitPort;
    private readonly IGetPromotionTypeUseCase _getPromotionTypeUseCase;
    private readonly IAccommodationPort _accommodationPort;
    private readonly IGetConditionUseCase _getConditionUseCase;
    private readonly ILogger<CreatePromotionUseCase> _logger;

    public CreatePromotionUseCase(IPromotionPort promotionPort,
        IPromotionConditionPort promotionConditionPort,
        IDbTransactionPort dbTransactionPort,
        IPromotionUnitPort promotionUnitPort,
        IGetPromotionTypeUseCase getPromotionTypeUseCase,
        IAccommodationPort accommodationPort,
        IGetConditionUseCase getConditionUseCase,
        ILogger<CreatePromotionUseCase> logger
        )
    {
        _promotionPort = promotionPort;
        _promotionConditionPort = promotionConditionPort;
        _dbTransactionPort = dbTransactionPort;
        _promotionUnitPort = promotionUnitPort;
        _accommodationPort = accommodationPort;
        _getPromotionTypeUseCase = getPromotionTypeUseCase;
        _getConditionUseCase = getConditionUseCase;
        _logger = logger;
    }

    public async Task<PromotionEntity> CreatePromotionAsync(CreatePromotionDto req)
    {
        // validate promotion type is exist
        var promotionType = await _getPromotionTypeUseCase.GetPromotionTypeByIdAsync(req.TypeId);
        if (promotionType == null)
        {
            _logger.LogError("Promotion type not found");
            throw new AppException(ErrorCode.PROMOTION_TYPE_NOT_FOUND);
        }

        // validate condition is exist
        var conditionIds = req.Conditions.Select(c => c.ConditionId).ToList();
        var conditions = await _getConditionUseCase.GetConditionsByIdsAsync(conditionIds);
        if (conditions.Count != conditionIds.Count)
        {
            _logger.LogError("some condition not found");
            throw new AppException(ErrorCode.PROMOTION_CONDITION_NOT_FOUND);
        }

        // validate unit of accommodation
        var accommodation = await _accommodationPort.GetAccommodationById(req.AccommodationId);
        if (accommodation == null)
        {
            _logger.LogError("Accommodation not found");
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }
        var requestUnitIds = req.Units.Select(u => u.UnitId).ToList();
        var unitIds = accommodation.Units.Select(u => u.Id).ToList();
        var invalidUnitIds = requestUnitIds.Except(unitIds).ToList();
        if (invalidUnitIds.Count > 0)
        {
            _logger.LogError("Invalid unit ids: {unitIds}", string.Join(", ", invalidUnitIds));
            throw new AppException(ErrorCode.GENERAL_FORBIDDEN);
        }

        // create promotion
        return await _dbTransactionPort.ExecuteInTransactionAsync(async () =>
        {
            var promotion = await _promotionPort.CreatePromotionAsync(req.ToEntity());

            var promotionConditions = req.Conditions.Select(c => c.ToEntity(promotion.Id)).ToList();
            await _promotionConditionPort.CreatePromotionConditionsAsync(promotionConditions);

            var promotionUnits = req.Units.Select(unit => unit.ToEntity(promotion.Id)).ToList();
            await _promotionUnitPort.CreatePromotionUnitsAsync(promotionUnits);

            return promotion;
        });

    }
}