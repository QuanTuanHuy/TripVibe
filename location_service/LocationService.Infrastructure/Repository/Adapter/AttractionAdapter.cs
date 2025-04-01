using LocationService.Core.Domain.Dto.Request;
using LocationService.Core.Domain.Entity;
using LocationService.Core.Port;
using LocationService.Infrastructure.Repository.Mapper;
using LocationService.Infrastructure.Repository.Specification;
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

        public async Task<AttractionEntity> GetAttractionByIdAsync(long id)
        {
            var model = await _dbContext.Attractions
                .FirstOrDefaultAsync(a => a.Id == id);
            return AttractionMapper.ToEntity(model);
        }

        public async Task<List<AttractionEntity>> GetAttractionsAsync(GetAttractionParams parameters)
        {
            var (query, paramList) = AttractionSpecification.ToGetAttractionsSpecification(parameters);
            var models = await _dbContext.Attractions
                .FromSqlRaw(query, paramList.ToArray())
                .ToListAsync();
            return models.Select(AttractionMapper.ToEntity).ToList();
        }

        public async Task<long> CountAttractionsAsync(GetAttractionParams parameters)
        {
            var (query, paramList) = AttractionSpecification.ToCountAttractionsSpecification(parameters);
            var result = await _dbContext.Database
                .SqlQueryRaw<long>(query, paramList.ToArray())
                .ToListAsync();
            return result.FirstOrDefault();
        }
    }
}