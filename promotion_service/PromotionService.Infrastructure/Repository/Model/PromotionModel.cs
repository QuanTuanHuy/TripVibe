using System.ComponentModel.DataAnnotations.Schema;

namespace PromotionService.Infrastructure.Repository.Model;

[Table("promotions")]
public class PromotionModel
{
    [Column("id")]
    public long Id { get; set; }

    [Column("created_by")]
    public long CreatedBy { get; set; }
    
    [Column("type_id")]
    public long TypeId { get; set; }
    
    [Column("accommodation_id")]
    public long AccommodationId { get; set; }
    
    [Column("name")]
    public string Name { get; set; }
    
    [Column("description")]
    public string Description { get; set; }
    
    [Column("is_active")]
    public bool IsActive { get; set; }
    
    [Column("discount_type")]
    public string DiscountType { get; set; }
    
    [Column("discount_value")]
    public long DiscountValue { get; set; }
    
    [Column("usage_limit")]
    public long UsageLimit { get; set; }
    
    [Column("usage_count")]
    public long UsageCount { get; set; }
    
    [Column("start_date")]
    public DateTime StartDate { get; set; }
    
    [Column("end_date")]
    public DateTime EndDate { get; set; }
}