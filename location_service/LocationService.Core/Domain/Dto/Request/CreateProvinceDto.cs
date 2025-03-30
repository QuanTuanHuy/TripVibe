using LocationService.Core.Domain.Entity;

namespace LocationService.Core.Domain.Dto.Request
{
    public class CreateProvinceDto
    {
        public string Name { get; set; }
        public string Code { get; set; }
        public int CountryId { get; set; }

        public ProvinceEntity ToEntity()
        {
            return new ProvinceEntity
            {
                Name = Name,
                Code = Code,
                CountryId = CountryId
            };
        }
    }
}