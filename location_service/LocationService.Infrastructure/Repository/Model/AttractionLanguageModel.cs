namespace LocationService.Infrastructure.Repository.Model
{
    using System.ComponentModel.DataAnnotations;
    using System.ComponentModel.DataAnnotations.Schema;

    [Table("attraction_languages")]
    public class AttractionLanguageModel : AuditModel
    {
        [Key]
        [Column("id")]
        public long Id { get; set; }

        [Column("attraction_id")]
        public long AttractionId { get; set; }

        [Column("language_id")]
        public long LanguageId { get; set; }
    }
}