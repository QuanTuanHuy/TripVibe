namespace LocationService.Infrastructure.Repository.Adapter
{
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Entity;
    using LocationService.Core.Port;
    using LocationService.Infrastructure.Repository.Mapper;
    using Microsoft.EntityFrameworkCore;

    public class CountryAdapter : ICountryPort
    {
        private readonly LocationDbContext _dbContext;
        public CountryAdapter(LocationDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<CountryEntity> GetCountryByCodeAsync(string code)
        {
            var countryModel = await _dbContext.Countries
                .AsNoTracking()
                .FirstOrDefaultAsync(c => c.Code == code);
            return CountryMapper.ToEntity(countryModel);
        }

        public async Task<CountryEntity> GetCountryByNameAsync(string name)
        {
            var countryModel = await _dbContext.Countries
                .AsNoTracking()
                .FirstOrDefaultAsync(c => c.Name == name);
            return CountryMapper.ToEntity(countryModel);
        }

        public async Task<CountryEntity> CreateCountryAsync(CountryEntity country)
        {
            var countryModel = CountryMapper.ToModel(country);
            _dbContext.Countries.Add(countryModel);
            await _dbContext.SaveChangesAsync();
            return CountryMapper.ToEntity(countryModel);
        }
    }
}