using LocationService.Core.Domain.Entity;

namespace LocationService.Core.Port
{
    public interface IAttractionLanguagePort
    {
        Task<List<AttractionLanguageEntity>> CreateAttractionLanguagesAsync(List<AttractionLanguageEntity> languages);
        Task<List<AttractionLanguageEntity>> GetByAttractionIdAsync(long attractionId);
    }
}