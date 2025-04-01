using LocationService.Core.Domain.Entity;

namespace LocationService.Core.Port
{
    public interface IAttractionPort
    {
        Task<AttractionEntity> CreateAttractionAsync(AttractionEntity attraction);
        Task<AttractionEntity> GetAttractionByNameAsync(string name);
    }
}