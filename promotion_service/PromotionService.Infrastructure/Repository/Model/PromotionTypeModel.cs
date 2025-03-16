using System.ComponentModel.DataAnnotations.Schema;

namespace PromotionService.Infrastructure.Repository.Model;

[Table("promotion_types")]
public class PromotionTypeModel
{
    public long Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string Description { get; set; } = string.Empty;
}