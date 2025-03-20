namespace PromotionService.Core.Domain.Entity;

public class PromotionEntity
{
    public long Id { get; set; }
    public long CreatedBy { get; set; }
    public long TypeId { get; set; }
    public long AccommodationId { get; set; }
    public string Name { get; set; }
    public string Description { get; set; }
    public bool IsActive { get; set; }
    public string DiscountType { get; set; }
    public long DiscountValue { get; set; }
    public long UsageLimit { get; set; }
    public long UsageCount { get; set; }
    public long StartDate { get; set; }
    public long EndDate { get; set; }
    
    public PromotionTypeEntity PromotionType { get; set; }
    public List<PromotionConditionEntity> Conditions { get; set; }
    public List<PromotionUnitEntity> Units { get; set; }
}