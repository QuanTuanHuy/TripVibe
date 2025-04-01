using LocationService.Core.Domain.Entity;

namespace LocationService.Core.Port
{
    using System.Threading.Tasks;

    public interface ICountryPort
    {
        Task<CountryEntity> GetCountryByCodeAsync(string code);
        Task<CountryEntity> GetCountryByNameAsync(string name);
        Task<CountryEntity> CreateCountryAsync(CountryEntity country);
        Task<CountryEntity> GetCountryByIdAsync(long id);
    }
}