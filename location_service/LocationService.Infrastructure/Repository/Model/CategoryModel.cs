using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace LocationService.Infrastructure.Repository.Model
{
    [Table("categories")]
    public class CategoryModel : AuditModel
    {
        [Key]
        [Column("id")]
        public long Id { get; set; }
        [Column("name")]
        public string Name { get; set; }
        [Column("description")]
        public string Description { get; set; }
        [Column("icon_url")]
        public string IconUrl { get; set; }
        [Column("is_active")]
        public bool IsActive { get; set; }
    }
}