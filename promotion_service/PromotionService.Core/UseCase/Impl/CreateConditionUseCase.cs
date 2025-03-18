using Microsoft.Extensions.Logging;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Domain.Mapper;
using PromotionService.Core.Domain.Port;
using PromotionService.Core.Port;

namespace PromotionService.Core.UseCase.Impl;

public class CreateConditionUseCase : ICreateConditionUseCase
{
    private readonly IConditionPort _conditionPort;
    private readonly IDbTransactionPort _transactionPort;
    private readonly ILogger<CreateConditionUseCase> _logger;

    public CreateConditionUseCase(
        IConditionPort conditionPort, 
        IDbTransactionPort transactionPort,
        ILogger<CreateConditionUseCase> logger
        )
    {
        _conditionPort = conditionPort;
        _transactionPort = transactionPort;
        _logger = logger;
    }
    
    public async Task<ConditionEntity> CreateConditionAsync(CreateConditionDto req)
    {
        var existedCondition = await _conditionPort.GetConditionByNameAsync(req.Name);
        if (existedCondition != null)
        {
            _logger.LogError("Condition name already exists");
            throw new Exception("Condition name already exists");
        }
        
        return await _transactionPort.ExecuteInTransactionAsync(async () =>
        {
            var entity = ConditionMapper.ToEntity(req);
            return await _conditionPort.CreateConditionAsync(entity);
        });
    }
}