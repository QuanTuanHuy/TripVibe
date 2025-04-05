namespace LocationService.Infrastructure.Repository.Model
{
    using System.ComponentModel.DataAnnotations;
    using System.ComponentModel.DataAnnotations.Schema;

    [Table("images")]
    public class ImageModel : AuditModel
    {
        [Key]
        [Column("id")]
        public long Id { get; set; }

        [Column("entity_id")]
        public long EntityId { get; set; }
        
        [Column("url")]
        public string Url { get; set; }

        [Column("entity_type")]
        public string EntityType { get; set; }

        [Column("is_primary")]
        public bool IsPrimary { get; set; }
    }
}