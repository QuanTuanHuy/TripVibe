using LocationService.Core.Domain.Entity;
using LocationService.Infrastructure.Repository.Model;

namespace LocationService.Infrastructure.Repository.Mapper
{
    public class ProvinceMapper
    {
        public static ProvinceModel ToModel(ProvinceEntity province) 
        {
            if (province == null) return null!;
            return new ProvinceModel
            {
                Id = province.Id,
                Name = province.Name,
                Code = province.Code,
                CountryId = province.CountryId,
            };
        }

        public static ProvinceEntity ToEntity(ProvinceModel province) 
        {
            if (province == null) return null!;
            return new ProvinceEntity
            {
                Id = province.Id,
                Name = province.Name,
                Code = province.Code,
                CountryId = province.CountryId,
            };
        }
    }
}