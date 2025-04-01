using LocationService.Core.Domain.Entity;

namespace LocationService.Core.Port
{
    public interface ILocationPort
    {
        Task<LocationEntity> CreateLocationAsync(LocationEntity location);
        Task<LocationEntity> GetLocationByIdAsync(long id);
    }
}