using LocationService.Core.Domain.Entity;
using LocationService.Core.Port;
using LocationService.Infrastructure.Repository.Mapper;
using Microsoft.EntityFrameworkCore;

namespace LocationService.Infrastructure.Repository.Adapter
{
    public class AttractionScheduleAdapter : IAttractionSchedulePort
    {
        private readonly LocationDbContext _dbContext;

        public AttractionScheduleAdapter(LocationDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<List<AttractionScheduleEntity>> CreateSchedulesAsync(List<AttractionScheduleEntity> schedules)
        {
            var models = schedules.Select(AttractionScheduleMapper.ToModel).ToList();
            await _dbContext.AttractionSchedules.AddRangeAsync(models);
            await _dbContext.SaveChangesAsync();
            return models.Select(AttractionScheduleMapper.ToEntity).ToList();
        }

        public async Task<List<AttractionScheduleEntity>> GetSchedulesByAttractionIdAsync(long attractionId)
        {
            var models = await _dbContext.AttractionSchedules
                .Where(s => s.AttractionId == attractionId)
                .ToListAsync();
            return models.Select(AttractionScheduleMapper.ToEntity).ToList();
        }
    }
    
}