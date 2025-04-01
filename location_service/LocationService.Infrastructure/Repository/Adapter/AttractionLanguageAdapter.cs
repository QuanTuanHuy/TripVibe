namespace LocationService.Infrastructure.Repository.Adapter
{
    using System.Collections.Generic;
    using System.Linq;
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Entity;
    using LocationService.Core.Port;
    using LocationService.Infrastructure.Repository.Mapper;
    using Microsoft.EntityFrameworkCore;

    public class AttractionLanguageAdapter : IAttractionLanguagePort
    {
        private readonly LocationDbContext _dbContext;

        public AttractionLanguageAdapter(LocationDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<List<AttractionLanguageEntity>> CreateAttractionLanguagesAsync(List<AttractionLanguageEntity> languages)
        {
            var models = languages.Select(AttractionLanguageMapper.ToModel).ToList();
            await _dbContext.AttractionLanguages.AddRangeAsync(models);
            await _dbContext.SaveChangesAsync();
            
            return models.Select(AttractionLanguageMapper.ToEntity).ToList();
        }

        public async Task<List<AttractionLanguageEntity>> GetByAttractionIdAsync(long attractionId)
        {
            var models = await _dbContext.AttractionLanguages
                .Where(al => al.AttractionId == attractionId)
                .ToListAsync();
                
            return models.Select(AttractionLanguageMapper.ToEntity).ToList();
        }

        public async Task<List<AttractionLanguageEntity>> GetByAttractionIds(List<long> attractionIds)
        {
            var models = await _dbContext.AttractionLanguages
                .Where(al => attractionIds.Contains(al.AttractionId))
                .ToListAsync();
                
            return models.Select(AttractionLanguageMapper.ToEntity).ToList();
        }
    }
}