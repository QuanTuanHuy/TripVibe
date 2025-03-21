namespace PromotionService.Core.Domain.Entity;

public class PromotionUnitEntity
{
    public long Id { get; set; }
    public long PromotionId { get; set; }
    public long UnitId { get; set; }
    public string UnitName { get; set; }
}