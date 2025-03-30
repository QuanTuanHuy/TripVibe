using LocationService.Core.Domain.Constant;
using LocationService.Core.Domain.Dto.Request;
using LocationService.Core.Domain.Entity;
using LocationService.Core.Domain.Port;
using LocationService.Core.Exception;
using LocationService.Core.Port;
using Microsoft.Extensions.Logging;

namespace LocationService.Core.UseCase
{
    public interface ICreateProvinceUseCase
    {
        Task<ProvinceEntity> CreateProvinceAsync(CreateProvinceDto req);
    }

    public class CreateProvinceUseCase : ICreateProvinceUseCase
    {
        private readonly IProvincePort _provincePort;
        private readonly IDbTransactionPort _dbTransactionPort;
        private readonly IGetCountryUseCase _getCountryUseCase;
        private readonly ILogger<CreateProvinceUseCase> _logger;

        public CreateProvinceUseCase(IProvincePort provincePort,
            IGetCountryUseCase getCountryUseCase,
            IDbTransactionPort dbTransactionPort,
            ILogger<CreateProvinceUseCase> logger)
        {
            _provincePort = provincePort;
            _dbTransactionPort = dbTransactionPort;
            _getCountryUseCase = getCountryUseCase;
            _logger = logger;
        }

        public async Task<ProvinceEntity> CreateProvinceAsync(CreateProvinceDto req)
        {
            var country = await _getCountryUseCase.GetCountryByIdAsync(req.CountryId);
            if (country == null)
            {
                _logger.LogError($"Country with ID {req.CountryId} not found.");
                throw new AppException(ErrorCode.COUNTRY_NOT_FOUND);
            }

            var existingProvince = await _provincePort.GetByCodeAsync(req.Code);
            if (existingProvince != null)
            {
                _logger.LogError($"Province with code {req.Code} already exists.");
                throw new AppException(ErrorCode.PROVINCE_CODE_ALREADY_EXISTS);
            }

            var province = req.ToEntity();
            await _dbTransactionPort.ExecuteInTransactionAsync(async () => {
                province = await _provincePort.CreateAsync(province);
            });

            return province;
        }
    }
}