using LocationService.Core.Domain.Entity;
using LocationService.Core.Port;

namespace LocationService.Core.UseCase
{
    public interface IGetCountryUseCase
    {
        Task<CountryEntity> GetCountryByIdAsync(long id);
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
    }
}