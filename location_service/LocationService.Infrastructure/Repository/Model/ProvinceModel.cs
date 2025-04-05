using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace LocationService.Infrastructure.Repository.Model
{
    [Table("provinces")]
    public class ProvinceModel : AuditModel
    {
        [Key]
        [Column("id")]
        public long Id { get; set; }
        [Column("name")]
        public string Name { get; set; } = null!;
        [Column("code")]
        public string Code { get; set; } = null!;
        [Column("country_id")]
        public long CountryId { get; set; }
    }
}