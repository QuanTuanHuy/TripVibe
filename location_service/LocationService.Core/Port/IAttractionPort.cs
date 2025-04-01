using LocationService.Core.Domain.Dto.Request;
using LocationService.Core.Domain.Entity;

namespace LocationService.Core.Port
{
    public interface IAttractionPort
    {
        Task<AttractionEntity> CreateAttractionAsync(AttractionEntity attraction);
        Task<AttractionEntity> GetAttractionByNameAsync(string name);
        Task<AttractionEntity> GetAttractionByIdAsync(long id);
        Task<List<AttractionEntity>> GetAttractionsAsync(GetAttractionParams parameters);
        Task<long> CountAttractionsAsync(GetAttractionParams parameters);
    }
}