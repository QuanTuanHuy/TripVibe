namespace PromotionService.Core.Domain.Entity;

public class ConditionEntity
{
    public long Id { get; set; }
    public string Name { get; set; }
    public string Description { get; set; }
    
    public ConditionEntity(string name, string description)
    {
        Name = name;
        Description = description;
    }
}