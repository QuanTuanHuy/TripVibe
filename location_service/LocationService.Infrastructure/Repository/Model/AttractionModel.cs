namespace LocationService.Infrastructure.Repository.Model
{
    using System.ComponentModel.DataAnnotations;
    using System.ComponentModel.DataAnnotations.Schema;

    [Table("attractions")]
    public class AttractionModel : AuditModel
    {
        [Key]
        [Column("id")]
        public long Id { get; set; }

        [Column("name")]
        public string Name { get; set; }

        [Column("description")]
        public string Description { get; set; }

        [Column("location_id")]
        public long LocationId { get; set; }

        [Column("category_id")]
        public long CategoryId { get; set; }
    }
}