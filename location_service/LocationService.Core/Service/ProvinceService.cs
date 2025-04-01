using LocationService.Core.Domain.Dto.Request;
using LocationService.Core.Domain.Dto.Response;
using LocationService.Core.Domain.Entity;
using LocationService.Core.UseCase;

namespace LocationService.Core.Service
{
    public interface IProvinceService
    {
        Task<ProvinceEntity> CreateProvinceAsync(CreateProvinceDto provinceDto);
        Task<(List<ProvinceEntity>, long)> GetProvincesAsync(ProvinceParams provinceParams);
    }

    public class ProvinceService : IProvinceService
    {
        private readonly ICreateProvinceUseCase _createProvinceUseCase;
        private readonly IGetProvinceUseCase _getProvinceUseCase;

        public ProvinceService(ICreateProvinceUseCase createProvinceUseCase,
            IGetProvinceUseCase getProvinceUseCase)
        {
            _createProvinceUseCase = createProvinceUseCase;
            _getProvinceUseCase = getProvinceUseCase;
        }

        public async Task<ProvinceEntity> CreateProvinceAsync(CreateProvinceDto provinceDto)
        {
            return await _createProvinceUseCase.CreateProvinceAsync(provinceDto);
        }

        public async Task<(List<ProvinceEntity>, long)> GetProvincesAsync(ProvinceParams provinceParams)
        {
            var provinces = await _getProvinceUseCase.GetProvinces(provinceParams);
            var count = await _getProvinceUseCase.CountProvinces(provinceParams);
            return (provinces, count);
        }
    }
}