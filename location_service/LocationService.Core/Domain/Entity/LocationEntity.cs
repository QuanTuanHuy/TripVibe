namespace LocationService.Core.Domain.Entity
{
    public class LocationEntity
    {
        public long Id { get; set; }
        public long CountryId { get; set; }
        public long ProvinceId { get; set; }
        public double Latitude { get; set; }
        public double Longitude { get; set; }
        public string Detail { get; set; }
        public string Name { get; set; }
    }
}