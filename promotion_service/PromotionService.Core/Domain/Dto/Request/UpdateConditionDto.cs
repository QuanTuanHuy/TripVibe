using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Domain.Dto.Request
{
    public class UpdateConditionDto
    {
        public string Name { get; set; }
        public string Description { get; set; }

        public ConditionEntity ToEntity(ConditionEntity existedCondition)
        {
            existedCondition.Name = Name;
            existedCondition.Description = Description;

            return existedCondition;
        }
    }
}