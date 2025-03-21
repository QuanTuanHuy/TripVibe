using System.ComponentModel.DataAnnotations.Schema;

namespace PromotionService.Infrastructure.Repository.Model;

[Table("promotion_units")]
public class PromotionUnitModel
{
    [Column("id")]
    public long Id { get; set; }
    [Column("promotion_id")]
    public long PromotionId { get; set; }
    [Column("unit_id")]
    public long UnitId { get; set; }
    [Column("unit_name")]
    public string UnitName { get; set; } = null!;
}