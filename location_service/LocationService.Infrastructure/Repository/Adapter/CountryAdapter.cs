namespace LocationService.Infrastructure.Repository.Adapter
{
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Dto.Request;
    using LocationService.Core.Domain.Entity;
    using LocationService.Core.Port;
    using LocationService.Infrastructure.Repository.Mapper;
    using LocationService.Infrastructure.Repository.Specification;
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

        public async Task<CountryEntity> GetCountryByIdAsync(long id)
        {
            var countryModel = await _dbContext.Countries
                .AsNoTracking()
                .FirstOrDefaultAsync(c => c.Id == id);
            return CountryMapper.ToEntity(countryModel);
        }

        public async Task<List<CountryEntity>> GetCountries(CountryParams countryParams)
        {
            var (sql, parameters) = CountrySpecification.ToGetCountrySpecification(countryParams);

            var countryModels = await _dbContext.Countries
                .FromSqlRaw(sql.ToString(), parameters.ToArray())
                .AsNoTracking()
                .ToListAsync();

            return countryModels.Select(CountryMapper.ToEntity).ToList();
        }

        public async Task<long> CountCountries(CountryParams countryParams)
        {
            var (sql, parameters) = CountrySpecification.ToCountCountrySpecification(countryParams);

            var result = await _dbContext.Database
                .SqlQueryRaw<long>(sql, parameters.ToArray())
                .ToListAsync();

            return result.FirstOrDefault();
        }
    }
}