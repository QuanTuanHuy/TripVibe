namespace LocationService.Infrastructure.Repository.Model
{
    using System.ComponentModel.DataAnnotations;
    using System.ComponentModel.DataAnnotations.Schema;

    [Table("locations")]
    public class LocationModel : AuditModel
    {
        [Key]
        [Column("id")]
        public long Id { get; set; }

        [Column("country_id")]
        public long CountryId { get; set; }

        [Column("province_id")]
        public long ProvinceId { get; set; }

        [Column("latitude")]
        public double Latitude { get; set; }

        [Column("longitude")]
        public double Longitude { get; set; }

        [Column("detail")]
        public string Detail { get; set; } = string.Empty;

        [Column("name")]
        public string Name { get; set; } = string.Empty;
    }
}