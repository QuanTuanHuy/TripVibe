using LocationService.Core.Domain.Entity;

namespace LocationService.Core.Port
{
    public interface IImagePort
    {
        Task<List<ImageEntity>> CreateImagesAsync(List<ImageEntity> images);
        Task<List<ImageEntity>> GetImagesByEntityIdAndEntityType(long entityId, string entityType);
    }
}