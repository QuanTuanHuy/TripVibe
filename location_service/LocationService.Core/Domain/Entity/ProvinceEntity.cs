namespace LocationService.Core.Domain.Entity
{
    public class ProvinceEntity
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public string Code { get; set; }
        public long CountryId { get; set; }
        public string? ThumbnailUrl { get; set; }
    }
}