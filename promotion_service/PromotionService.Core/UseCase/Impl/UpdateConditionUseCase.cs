using Microsoft.Extensions.Logging;
using PromotionService.Core.Domain.Constant;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Domain.Port;
using PromotionService.Core.Exception;
using PromotionService.Core.Port;

namespace PromotionService.Core.UseCase.Impl
{
    public class UpdateConditionUseCase : IUpdateConditionUseCase
    {
        private readonly IConditionPort _conditionPort;
        private readonly IDbTransactionPort _dbTransactionPort;
        private readonly ILogger<UpdateConditionUseCase> _logger;

        public UpdateConditionUseCase(
            IConditionPort conditionPort,
            IDbTransactionPort dbTransactionPort,
            ILogger<UpdateConditionUseCase> logger
            )
        {
            _conditionPort = conditionPort;
            _dbTransactionPort = dbTransactionPort;
            _logger = logger;
        }

        public async Task<ConditionEntity> UpdateConditionAsync(long id, UpdateConditionDto req)
        {
            var existedCondition = await _conditionPort.GetConditionByIdAsync(id);
            if (existedCondition == null)
            {
                _logger.LogError("Condition not found, ID: {Id}", id);
                throw new AppException(ErrorCode.CONDITION_NOT_FOUND);
            }

            var existedName = await _conditionPort.GetConditionByNameAsync(req.Name);
            if (existedName != null && existedName.Id != id)
            {
                _logger.LogError("Condition name already exists, Name: {Name}", req.Name);
                throw new AppException(ErrorCode.CONDITION_NAME_EXIST);
            }

            existedCondition = req.ToEntity(existedCondition);
            return await _dbTransactionPort.ExecuteInTransactionAsync(async () =>
            {
                return await _conditionPort.UpdateConditionAsync(existedCondition);
            });
        }
    }
}