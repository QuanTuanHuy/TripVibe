namespace PromotionService.Core.Domain.Entity;

public class PromotionTypeEntity
{
    public long Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string Description { get; set; } = string.Empty;
}