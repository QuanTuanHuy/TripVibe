using LocationService.Core.Domain.Entity;
using LocationService.Core.Port;
using LocationService.Infrastructure.Repository.Mapper;
using Microsoft.EntityFrameworkCore;

namespace LocationService.Infrastructure.Repository.Adapter
{
    public class LocationAdapter : ILocationPort
    {
        private readonly LocationDbContext _dbContext;

        public LocationAdapter(LocationDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<LocationEntity> CreateLocationAsync(LocationEntity location)
        {
            var model = LocationMapper.ToModel(location);
            _dbContext.Locations.Add(model);
            await _dbContext.SaveChangesAsync();
            return LocationMapper.ToEntity(model);
        }

        public async Task<LocationEntity> GetLocationByIdAsync(long id)
        {
            var model = await _dbContext.Locations.FindAsync(id);
            return LocationMapper.ToEntity(model);
        }

        public async Task<List<LocationEntity>> GetLocationsByIdsAsync(List<long> ids)
        {
            var models = await _dbContext.Locations
                .Where(l => ids.Contains(l.Id))
                .ToListAsync();
            return models.Select(LocationMapper.ToEntity).ToList();
        }
    }
}