using LocationService.Core.Domain.Entity;
using LocationService.Core.Port;
using LocationService.Infrastructure.Repository.Mapper;
using Microsoft.EntityFrameworkCore;

namespace LocationService.Infrastructure.Repository.Adapter
{
    public class AttractionAdapter : IAttractionPort
    {
        private readonly LocationDbContext _dbContext;

        public AttractionAdapter(LocationDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<AttractionEntity> CreateAttractionAsync(AttractionEntity attraction)
        {
            var model = AttractionMapper.ToModel(attraction);
            _dbContext.Attractions.Add(model);
            await _dbContext.SaveChangesAsync();
            return AttractionMapper.ToEntity(model);
        }

        public async Task<AttractionEntity> GetAttractionByNameAsync(string name)
        {
            var model = await _dbContext.Attractions
                .FirstOrDefaultAsync(a => a.Name == name);
            return AttractionMapper.ToEntity(model);
        }
    }
}