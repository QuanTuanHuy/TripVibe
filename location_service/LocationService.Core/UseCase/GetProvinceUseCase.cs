using LocationService.Core.Domain.Dto.Request;
using LocationService.Core.Domain.Entity;
using LocationService.Core.Domain.Port;

namespace LocationService.Core.UseCase
{
    public interface IGetProvinceUseCase
    {
        Task<List<ProvinceEntity>> GetProvinces(ProvinceParams provinceParams);
        Task<long> CountProvinces(ProvinceParams provinceParams);
    }

    public class GetProvinceUseCase : IGetProvinceUseCase
    {
        private readonly IProvincePort _provincePort;

        public GetProvinceUseCase(IProvincePort provincePort)
        {
            _provincePort = provincePort;
        }

        public async Task<List<ProvinceEntity>> GetProvinces(ProvinceParams provinceParams)
        {
            return await _provincePort.GetProvinces(provinceParams);
        }

        public async Task<long> CountProvinces(ProvinceParams provinceParams)
        {
            return await _provincePort.CountProvinces(provinceParams);
        }
    }
}