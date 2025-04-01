using LocationService.Core.Domain.Constant;
using LocationService.Core.Domain.Entity;
using LocationService.Core.Exception;
using LocationService.Core.Port;

namespace LocationService.Core.UseCase
{
    public interface IGetLocationUseCase
    {
        Task<LocationEntity> GetLocationByIdAsync(long id);
        Task<List<LocationEntity>> GetLocationsByIdsAsync(List<long> ids);
    }

    public class GetLocationUseCase : IGetLocationUseCase
    {
        private readonly ILocationPort _locationPort;

        public GetLocationUseCase(ILocationPort locationPort)
        {
            _locationPort = locationPort;
        }

        public async Task<LocationEntity> GetLocationByIdAsync(long id)
        {
            var location = await _locationPort.GetLocationByIdAsync(id);
            if (location == null) 
            {
                throw new AppException(ErrorCode.LOCATION_NOT_FOUND);
            }
            return location;
        }

        public async Task<List<LocationEntity>> GetLocationsByIdsAsync(List<long> ids)
        {
            return await _locationPort.GetLocationsByIdsAsync(ids);
        }
    }
}