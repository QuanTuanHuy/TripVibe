namespace PromotionService.Core.Domain.Entity;

public class PromotionConditionEntity
{
    public long Id { get; set; }
    public long PromotionId { get; set; }
    public long ConditionId { get; set; }
    public int ConditionValue { get; set; }

    public ConditionEntity Condition { get; set; }
}