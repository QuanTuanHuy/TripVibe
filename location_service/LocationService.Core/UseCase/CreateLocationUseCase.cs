using LocationService.Core.Domain.Constant;
using LocationService.Core.Domain.Dto.Request;
using LocationService.Core.Domain.Entity;
using LocationService.Core.Exception;
using LocationService.Core.Port;

namespace LocationService.Core.UseCase
{
    public interface ICreateLocationUseCase
    {
        Task<LocationEntity> CreateLocationAsync(CreateLocationDto req);
    }

    public class CreateLocationUseCase : ICreateLocationUseCase
    {
        private readonly ILocationPort _locationPort;
        private readonly IDbTransactionPort _dbTransactionPort;
        private readonly IGetCountryUseCase _getCountryUseCase;
        private readonly IGetProvinceUseCase _getProvinceUseCase;

        public CreateLocationUseCase(ILocationPort locationPort, IDbTransactionPort dbTransactionPort, IGetCountryUseCase getCountryUseCase, IGetProvinceUseCase getProvinceUseCase)
        {
            _locationPort = locationPort;
            _dbTransactionPort = dbTransactionPort;
            _getCountryUseCase = getCountryUseCase;
            _getProvinceUseCase = getProvinceUseCase;
        }

        public async Task<LocationEntity> CreateLocationAsync(CreateLocationDto req)
        {
            var country = await _getCountryUseCase.GetCountryByIdAsync(req.CountryId);
            if (country == null)
            {
                throw new AppException(ErrorCode.COUNTRY_NOT_FOUND);
            }

            var province = await _getProvinceUseCase.GetProvinceByIdAsync(req.ProvinceId);
            if (province == null)
            {
                throw new AppException(ErrorCode.PROVINCE_NOT_FOUND);
            }

            var location = req.ToEntity();
            await _dbTransactionPort.ExecuteInTransactionAsync(async () =>
            {
                location = await _locationPort.CreateLocationAsync(location);
            });

            return location;
        }
    }
}