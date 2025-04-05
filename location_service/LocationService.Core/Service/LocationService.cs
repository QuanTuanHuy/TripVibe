using LocationService.Core.Domain.Dto.Request;
using LocationService.Core.Domain.Entity;
using LocationService.Core.UseCase;

namespace LocationService.Core.Service
{
    public interface ILocationService
    {
        Task<LocationEntity> CreateLocationAsync(CreateLocationDto req);
    }

    public class LocationAppService : ILocationService
    {
        private readonly ICreateLocationUseCase _createLocationUseCase;

        public LocationAppService(ICreateLocationUseCase createLocationUseCase)
        {
            _createLocationUseCase = createLocationUseCase;
        }

        public async Task<LocationEntity> CreateLocationAsync(CreateLocationDto req)
        {
            return await _createLocationUseCase.CreateLocationAsync(req);
        }
    }
}