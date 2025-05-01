using LocationService.Core.Domain.Entity;

namespace LocationService.Core.Port
{
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Dto.Request;

    public interface ICountryPort
    {
        Task<CountryEntity> GetCountryByCodeAsync(string code);
        Task<CountryEntity> GetCountryByNameAsync(string name);
        Task<CountryEntity> CreateCountryAsync(CountryEntity country);
        Task<CountryEntity> GetCountryByIdAsync(long id);
        Task<List<CountryEntity>> GetCountries(CountryParams countryParams);
        Task<long> CountCountries(CountryParams countryParams);
    }
}