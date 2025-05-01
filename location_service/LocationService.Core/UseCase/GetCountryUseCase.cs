using LocationService.Core.Domain.Dto.Request;
using LocationService.Core.Domain.Entity;
using LocationService.Core.Port;

namespace LocationService.Core.UseCase
{
    public interface IGetCountryUseCase
    {
        Task<CountryEntity> GetCountryByIdAsync(long id);
        Task<(List<CountryEntity>, long)> GetCountries(CountryParams countryParams);
    }

    public class GetCountryUseCase : IGetCountryUseCase
    {
        private readonly ICountryPort _countryPort;

        public GetCountryUseCase(ICountryPort countryPort)
        {
            _countryPort = countryPort;
        }

        public async Task<CountryEntity> GetCountryByIdAsync(long id)
        {
            return await _countryPort.GetCountryByIdAsync(id);
        }

        public async Task<(List<CountryEntity>, long)> GetCountries(CountryParams countryParams)
        {
            var countries = await _countryPort.GetCountries(countryParams);
            var count = await _countryPort.CountCountries(countryParams);
            return (countries, count);
        }
    }
}