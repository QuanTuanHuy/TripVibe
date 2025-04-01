namespace LocationService.Infrastructure.Repository.Adapter
{
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Entity;
    using LocationService.Core.Port;
    using LocationService.Infrastructure.Repository.Mapper;
    using Microsoft.EntityFrameworkCore;

    public class LanguageAdapter : ILanguagePort
    {
        private readonly LocationDbContext _dbContext;

        public LanguageAdapter(LocationDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<LanguageEntity> CreateLanguageAsync(LanguageEntity language)
        {
            var model = LanguageMapper.ToModel(language);
            _dbContext.Languages.Add(model);
            await _dbContext.SaveChangesAsync();
            return LanguageMapper.ToEntity(model);
        }

        public async Task<LanguageEntity> GetLanguageByIdAsync(long id)
        {
            var model = await _dbContext.Languages.FindAsync(id);
            return LanguageMapper.ToEntity(model);
        }

        public async Task<LanguageEntity> GetLanguageByCodeAsync(string code)
        {
            var model = await _dbContext.Languages
                .AsNoTracking()
                .FirstOrDefaultAsync(l => l.Code == code);
            return LanguageMapper.ToEntity(model);
        }

        public async Task<LanguageEntity> GetLanguageByNameAsync(string name)
        {
            var model = await _dbContext.Languages
                .AsNoTracking()
                .FirstOrDefaultAsync(l => l.Name == name);
            return LanguageMapper.ToEntity(model);
        }
    }
}