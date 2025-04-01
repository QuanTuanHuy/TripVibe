namespace LocationService.Core.Domain.Port
{
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Dto.Request;
    using LocationService.Core.Domain.Entity;

    public interface IProvincePort
    {
        Task<ProvinceEntity> CreateAsync(ProvinceEntity province);
        Task<ProvinceEntity> GetByCodeAsync(string code);
        Task<List<ProvinceEntity>> GetProvinces(ProvinceParams provinceParams);
        Task<long> CountProvinces(ProvinceParams provinceParams);
    }
}