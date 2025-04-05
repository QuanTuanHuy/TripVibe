using System.ComponentModel.DataAnnotations.Schema;

namespace PromotionService.Infrastructure.Repository.Model;

[Table("promotion_types")]
public class PromotionTypeModel
{
    [Column("id")]
    public long Id { get; set; }
    
    [Column("name")]
    public string Name { get; set; }
    
    [Column("description")]
    public string Description { get; set; }
}