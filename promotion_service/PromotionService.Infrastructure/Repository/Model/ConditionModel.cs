namespace PromotionService.Infrastructure.Repository.Model;

using System.ComponentModel.DataAnnotations.Schema;


[Table("conditions")]
public class ConditionModel
{
    [Column("id")]
    public long Id { get; set; }
    
    [Column("name")]
    public string Name { get; set; }
    
    [Column("description")]
    public string Description { get; set; }

    public ConditionModel(string name, string description)
    {
        Name = name;
        Description = description;
    }
}