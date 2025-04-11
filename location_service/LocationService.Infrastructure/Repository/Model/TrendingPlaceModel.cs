using System.ComponentModel.DataAnnotations.Schema;

namespace LocationService.Infrastructure.Repository.Model
{
    [Table("trending_places")]
    public class TrendingPlaceModel : AuditModel
    {
        [Column("id")]
        public long Id { get; set; }
        [Column("name")]
        public string Name { get; set; }
        [Column("description")]
        public string Description { get; set; }
        [Column("type")]
        public string Type { get; set; } 
        [Column("reference_id")]
        public long ReferenceId { get; set; } 
        [Column("rank")]
        public int Rank { get; set; }
        [Column("start_date")]
        public DateTime StartDate { get; set; }
        [Column("end_date")]
        public DateTime EndDate { get; set; }
        [Column("is_active")]
        public bool IsActive { get; set; }
        [Column("tags")]
        public string Tags { get; set; }
        [Column("image_url")]
        public string ImageUrl { get; set; }
        [Column("score")]
        public double Score { get; set; }
        [Column("metadata")]
        public string Metadata { get; set; } 
    }
}