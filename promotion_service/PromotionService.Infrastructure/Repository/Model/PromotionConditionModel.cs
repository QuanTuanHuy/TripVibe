using System.ComponentModel.DataAnnotations.Schema;

namespace PromotionService.Infrastructure.Repository.Model;

[Table("promotion_conditions")]
public class PromotionConditionModel
{
    [Column("id")]
    public long Id { get; set; }
    
    [Column("promotion_id")]
    public long PromotionId { get; set; }
    
    [Column("condition_id")]
    public long ConditionId { get; set; }

    [Column("condition_value")]
    public int ConditionValue { get; set; }
}