namespace PromotionService.Core.UseCase.Impl
{
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using PromotionService.Core.Domain.Entity;
    using PromotionService.Core.Port;

    public class GetConditionUseCase : IGetConditionUseCase
    {
        private readonly IConditionPort _conditionPort;

        public GetConditionUseCase(IConditionPort conditionPort)
        {
            _conditionPort = conditionPort;
        }
        public Task<List<ConditionEntity>> GetConditionsByIdsAsync(List<long> ids)
        {
            return _conditionPort.GetConditionsByIdsAsync(ids);
        }
    } 
}