using LocationService.Core.Domain.Entity;
using LocationService.Core.Port;
using LocationService.Infrastructure.Repository.Mapper;
using Microsoft.EntityFrameworkCore;

namespace LocationService.Infrastructure.Repository.Adapter
{
    public class ImageAdapter : IImagePort
    {
        private readonly LocationDbContext _dbContext;

        public ImageAdapter(LocationDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<List<ImageEntity>> CreateImagesAsync(List<ImageEntity> images)
        {
            var models = images.Select(ImageMapper.ToModel).ToList();
            await _dbContext.Images.AddRangeAsync(models);
            await _dbContext.SaveChangesAsync();
            return models.Select(ImageMapper.ToEntity).ToList();
        }

        public async Task<List<ImageEntity>> GetImagesByEntityIdAndEntityType(long entityId, string entityType)
        {
            var models = await _dbContext.Images
                .Where(x => x.EntityId == entityId && x.EntityType == entityType)
                .ToListAsync();
            return models.Select(ImageMapper.ToEntity).ToList();
        }
    }
}