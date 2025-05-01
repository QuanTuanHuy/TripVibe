namespace LocationService.Core.Service
{
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Dto.Request;
    using LocationService.Core.Domain.Entity;
    using LocationService.Core.UseCase;

    public interface ICountryService
    {
        Task<CountryEntity> CreateCountryAsync(CreateCountryDto countryDto);
        Task<(List<CountryEntity>, long)> GetCountries(CountryParams countryParams);
    }

    public class CountryService : ICountryService
    {
        private readonly ICreateCountryUseCase _createCountryUseCase;
        private readonly IGetCountryUseCase _getCountryUseCase;

        public CountryService(ICreateCountryUseCase createCountryUseCase, IGetCountryUseCase getCountryUseCase)
        {
            _createCountryUseCase = createCountryUseCase;
            _getCountryUseCase = getCountryUseCase;
        }

        public async Task<CountryEntity> CreateCountryAsync(CreateCountryDto countryDto)
        {
            return await _createCountryUseCase.CreateCountryAsync(countryDto);
        }

        public async Task<(List<CountryEntity>, long)> GetCountries(CountryParams countryParams)
        {
            return await _getCountryUseCase.GetCountries(countryParams);
        }
    }
}