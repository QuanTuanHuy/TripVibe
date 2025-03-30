namespace LocationService.Infrastructure.Repository.Model
{
    using System.ComponentModel.DataAnnotations;
    using System.ComponentModel.DataAnnotations.Schema;

    [Table("countries")]
    public class CountryModel
    {
        [Key]
        [Column("id")]
        public long Id { get; set; }

        [Column("name")]
        public string Name { get; set; }

        [Column("code")]
        public string Code { get; set; }

        [Column("currency")]
        public string Currency { get; set; }

        [Column("timezone")]
        public string Timezone { get; set; }

        [Column("language")]
        public string Language { get; set; }

        [Column("region")]
        public string Region { get; set; }

        [Column("sub_region")]
        public string SubRegion { get; set; }

        [Column("flag_url")]
        public string FlagUrl { get; set; }
    }
}