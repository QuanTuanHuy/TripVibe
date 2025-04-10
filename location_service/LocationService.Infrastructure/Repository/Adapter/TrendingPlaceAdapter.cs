using LocationService.Core.Domain.Entity;
using LocationService.Core.Port;
using LocationService.Infrastructure.Repository.Mapper;
using Microsoft.EntityFrameworkCore;

namespace LocationService.Infrastructure.Repository.Adapter
{
    public class TrendingPlaceAdapter : ITrendingPlacePort
    {
        private readonly LocationDbContext _dbContext;
        public TrendingPlaceAdapter(LocationDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<TrendingPlaceEntity> CreateTrendingPlace(TrendingPlaceEntity trendingPlace)
        {
            var model = TrendingPlaceMapper.ToModel(trendingPlace);
            _dbContext.TrendingPlaces.Add(model);
            await _dbContext.SaveChangesAsync();
            return TrendingPlaceMapper.ToEntity(model);
        }

        public async Task<TrendingPlaceEntity> GetByReferenceIdAndType(long referenceId, string type)
        {
            var model = await _dbContext.TrendingPlaces
                .FirstOrDefaultAsync(x => x.ReferenceId == referenceId && x.Type == type);
            return TrendingPlaceMapper.ToEntity(model);
        }

        public async Task<(List<TrendingPlaceEntity>, long)> GetTrendingPlacesByType(string type, int page, int size)
        {
            var trendingPlaces = await _dbContext.TrendingPlaces
                .AsNoTracking()
                .Where(x => x.Type == type && x.IsActive)
                .OrderByDescending(x => x.Rank)
                .Skip(page * size)
                .Take(size)
                .ToListAsync();
            
            var total = await _dbContext.TrendingPlaces.CountAsync(x => x.Type == type && x.IsActive);
            return (TrendingPlaceMapper.ToEntityList(trendingPlaces), total);
        }

        public async Task<(List<TrendingPlaceEntity>, long)> GetTrendingPlacesAsync(int pageSize, int offset)
        {
            var trendingPlaces = await _dbContext.TrendingPlaces
                .AsNoTracking()
                .Where(x => x.IsActive)
                .OrderByDescending(x => x.Rank)
                .Skip(offset * pageSize)
                .Take(pageSize)
                .ToListAsync();
            
            var total = await _dbContext.TrendingPlaces.CountAsync(x => x.IsActive);
            
            return (TrendingPlaceMapper.ToEntityList(trendingPlaces), total);
        }

        public async Task<TrendingPlaceEntity> GetTrendingPlaceById(long id)
        {
            var model = await _dbContext.TrendingPlaces
                .AsNoTracking()
                .FirstOrDefaultAsync(x => x.Id == id && x.IsActive);
            return TrendingPlaceMapper.ToEntity(model);
        }
    }
}