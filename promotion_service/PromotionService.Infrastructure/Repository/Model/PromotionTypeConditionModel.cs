using System.ComponentModel.DataAnnotations.Schema;

namespace PromotionService.Infrastructure.Repository.Model
{
    [Table("promotion_type_conditions")]
    public class PromotionTypeConditionModel
    {
        [Column("id")]
        public long Id { get; set; }
        [Column("promotion_type_id")]
        public long PromotionTypeId { get; set; }
        [Column("condition_id")]
        public long ConditionId { get; set; }
        [Column("default_value")]
        public long DefaultValue { get; set; }
    }
}