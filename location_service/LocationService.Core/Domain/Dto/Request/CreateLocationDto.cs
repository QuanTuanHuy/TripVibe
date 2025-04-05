using LocationService.Core.Domain.Entity;

namespace LocationService.Core.Domain.Dto.Request
{
    public class CreateLocationDto
    {
        public long CountryId { get; set; }
        public long ProvinceId { get; set; }
        public double Latitude { get; set; }
        public double Longitude { get; set; }
        public string Detail { get; set; }
        public string Name { get; set; }

        public LocationEntity ToEntity()
        {
            return new LocationEntity
            {
                CountryId = CountryId,
                ProvinceId = ProvinceId,
                Latitude = Latitude,
                Longitude = Longitude,
                Detail = Detail,
                Name = Name
            };
        }
    }
}