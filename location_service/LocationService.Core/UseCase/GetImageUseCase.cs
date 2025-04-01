using LocationService.Core.Domain.Entity;
using LocationService.Core.Port;

namespace LocationService.Core.UseCase
{
    public interface IGetImageUseCase
    {
        Task<List<ImageEntity>> GetImagesByEntityIdAndEntityType(long entityId, string entityType);
    }

    public class GetImageUseCase : IGetImageUseCase
    {
        private readonly IImagePort _imagePort;

        public GetImageUseCase(IImagePort imagePort)
        {
            _imagePort = imagePort;
        }

        public async Task<List<ImageEntity>> GetImagesByEntityIdAndEntityType(long entityId, string entityType)
        {
            return await _imagePort.GetImagesByEntityIdAndEntityType(entityId, entityType);
        }
    }
}