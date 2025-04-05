using LocationService.Core.Domain.Entity;

namespace LocationService.Core.Domain.Dto.Request
{
    public class CreateCountryDto
    {
        public string Name { get; set; }
        public string Code { get; set; }
        public string Currency { get; set; }
        public string Timezone { get; set; }
        public string Language { get; set; }
        public string Region { get; set; }
        public string SubRegion { get; set; }
        public string FlagUrl { get; set; }

        public CountryEntity toEntity()
        {
            return new CountryEntity
            {
                Name = Name,
                Code = Code,
                Currency = Currency,
                Timezone = Timezone,
                Language = Language,
                Region = Region,
                SubRegion = SubRegion,
                FlagUrl = FlagUrl
            };
        }
    }
}
