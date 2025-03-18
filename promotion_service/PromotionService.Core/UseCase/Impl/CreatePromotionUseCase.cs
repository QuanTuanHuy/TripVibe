using Microsoft.Extensions.Logging;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Domain.Port;
using PromotionService.Core.Port;

namespace PromotionService.Core.UseCase.Impl;

public class CreatePromotionUseCase : ICreatePromotionUseCase
{
    private readonly IPromotionPort _promotionPort;
    private readonly IPromotionConditionPort _promotionConditionPort;
    private readonly IDbTransactionPort _dbTransactionPort;
    private readonly IGetPromotionTypeUseCase _getPromotionTypeUseCase;
    private readonly IGetConditionUseCase _getConditionUseCase;
    private readonly ILogger<CreatePromotionUseCase> _logger;

    public CreatePromotionUseCase(IPromotionPort promotionPort,
        IPromotionConditionPort promotionConditionPort,
        IDbTransactionPort dbTransactionPort,
        IGetPromotionTypeUseCase getPromotionTypeUseCase,
        IGetConditionUseCase getConditionUseCase,
        ILogger<CreatePromotionUseCase> logger
        )
    {
        _promotionPort = promotionPort;
        _promotionConditionPort = promotionConditionPort;
        _dbTransactionPort = dbTransactionPort;
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
            throw new Exception("Promotion type not found");
        }

        // validate condition is exist
        var conditionIds = req.Conditions.Select(c => c.ConditionId).ToList();
        var conditions = await _getConditionUseCase.GetConditionsByIdsAsync(conditionIds);
        if (conditions.Count != conditionIds.Count)
        {
            _logger.LogError("some condition not found");
            throw new Exception("some condition not found");
        }

        // create promotion
        return await _dbTransactionPort.ExecuteInTransactionAsync(async () =>
        {
            var promotion = await _promotionPort.CreatePromotionAsync(req.ToEntity());

            // create promotion condition list
            var promotionConditions = new List<PromotionConditionEntity>();
            foreach (var condition in req.Conditions)
            {
                var promotionCondition = new PromotionConditionEntity
                {
                    PromotionId = promotion.Id,
                    ConditionId = condition.ConditionId,
                    ConditionValue = condition.ConditionValue
                };
                promotionConditions.Add(promotionCondition);
            }
            await _promotionConditionPort.CreatePromotionConditionsAsync(promotionConditions);

            return promotion;
        });

    }
}