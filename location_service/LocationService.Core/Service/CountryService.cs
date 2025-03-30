namespace LocationService.Core.Service
{
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Dto.Request;
    using LocationService.Core.Domain.Entity;
    using LocationService.Core.UseCase;

    public interface ICountryService
    {
        Task<CountryEntity> CreateCountryAsync(CreateCountryDto countryDto);
    }

    public class CountryService : ICountryService
    {
        private readonly ICreateCountryUseCase _createCountryUseCase;

        public CountryService(ICreateCountryUseCase createCountryUseCase)
        {
            _createCountryUseCase = createCountryUseCase;
        }

        public async Task<CountryEntity> CreateCountryAsync(CreateCountryDto countryDto)
        {
            return await _createCountryUseCase.CreateCountryAsync(countryDto);
        }
    }
}