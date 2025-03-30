using LocationService.Core.Domain.Entity;
using LocationService.Core.Port;

namespace LocationService.Core.UseCase
{
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Constant;
    using LocationService.Core.Domain.Dto.Request;
    using LocationService.Core.Exception;

    public interface ICreateCountryUseCase
    {
        Task<CountryEntity> CreateCountryAsync(CreateCountryDto req);
    }

    public class CreateCountryUseCase : ICreateCountryUseCase
    {
        private readonly ICountryPort _countryPort;
        private readonly IDbTransactionPort _dbTransactionPort;
        
        public CreateCountryUseCase(ICountryPort countryPort, IDbTransactionPort dbTransactionPort)
        {
            _dbTransactionPort = dbTransactionPort;
            _countryPort = countryPort;
        }

        public async Task<CountryEntity> CreateCountryAsync(CreateCountryDto req)
        {
            var existingCountry = await _countryPort.GetCountryByCodeAsync(req.Code);
            if (existingCountry != null)
            {
                throw new AppException(ErrorCode.COUNTRY_CODE_ALREADY_EXISTS);
            }
            
            existingCountry = await _countryPort.GetCountryByNameAsync(req.Name);
            if (existingCountry != null)
            {
                throw new AppException(ErrorCode.COUNTRY_NAME_ALREADY_EXISTS);
            }

            var country = req.toEntity();
            await _dbTransactionPort.ExecuteInTransactionAsync(async () =>
            {
                country = await _countryPort.CreateCountryAsync(country);
            });

            return country;
        }
    }
}