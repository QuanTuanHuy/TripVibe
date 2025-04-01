namespace LocationService.Core.Port
{
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Entity;

    public interface ILanguagePort
    {
        Task<LanguageEntity> CreateLanguageAsync(LanguageEntity language);
        Task<LanguageEntity> GetLanguageByIdAsync(long id);
        Task<List<LanguageEntity>> GetLanguagesByIdsAsync(List<long> ids);
        Task<LanguageEntity> GetLanguageByCodeAsync(string code);
        Task<LanguageEntity> GetLanguageByNameAsync(string name);
    }
}