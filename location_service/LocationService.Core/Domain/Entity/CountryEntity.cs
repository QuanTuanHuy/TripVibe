namespace LocationService.Core.Domain.Entity
{
    public class CountryEntity
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public string Code { get; set; }
        public string Currency { get; set; }
        public string Timezone { get; set; }
        public string Language { get; set; }
        public string Region { get; set; }
        public string SubRegion { get; set; }
        public string FlagUrl { get; set; }
    }
}