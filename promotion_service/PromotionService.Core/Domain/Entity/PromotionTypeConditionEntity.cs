namespace PromotionService.Core.Domain.Entity
{
    public class PromotionTypeConditionEntity
    {
        public long Id { get; set; }
        public long PromotionTypeId { get; set; }
        public long ConditionId { get; set; }
        public long DefaultValue { get; set; }

        public ConditionEntity Condition { get; set; }
    }
}