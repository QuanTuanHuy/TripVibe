using LocationService.Core.Domain.Entity;
using LocationService.Infrastructure.Repository.Model;

namespace LocationService.Infrastructure.Repository.Mapper
{
    public class LocationMapper
    {
        public static LocationModel ToModel(LocationEntity entity)
        {
            if (entity == null) return null!;
            return new LocationModel
            {
                Id = entity.Id,
                CountryId = entity.CountryId,
                ProvinceId = entity.ProvinceId,
                Latitude = entity.Latitude,
                Longitude = entity.Longitude,
                Detail = entity.Detail,
                Name = entity.Name
            };
        }

        public static LocationEntity ToEntity(LocationModel model)
        {
            if (model == null) return null!;
            return new LocationEntity
            {
                Id = model.Id,
                CountryId = model.CountryId,
                ProvinceId = model.ProvinceId,
                Latitude = model.Latitude,
                Longitude = model.Longitude,
                Detail = model.Detail,
                Name = model.Name
            };
        }
    }
}