namespace LocationService.Infrastructure.Repository.Mapper
{
    using LocationService.Core.Domain.Entity;
    using LocationService.Infrastructure.Repository.Model;

    public class CountryMapper
    {
        public static CountryModel ToModel(CountryEntity country) 
        {
            if (country == null) return null!;
            return new CountryModel
            {
                Name = country.Name,
                Code = country.Code,
                Currency = country.Currency,
                Timezone = country.Timezone,
                Language = country.Language,
                Region = country.Region,
                SubRegion = country.SubRegion,
                FlagUrl = country.FlagUrl
            };
        }

        public static CountryEntity ToEntity(CountryModel country) 
        {
            if (country == null) return null!;
            return new CountryEntity
            {
                Id = country.Id,
                Name = country.Name,
                Code = country.Code,
                Currency = country.Currency,
                Timezone = country.Timezone,
                Language = country.Language,
                Region = country.Region,
                SubRegion = country.SubRegion,
                FlagUrl = country.FlagUrl
            };
        }
    }
}