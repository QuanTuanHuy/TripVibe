namespace LocationService.Infrastructure.Repository.Adapter
{
    using LocationService.Core.Domain.Entity;
    using LocationService.Infrastructure.Repository.Mapper;
    using LocationService.Core.Domain.Port;
    using Microsoft.EntityFrameworkCore;
    using LocationService.Core.Domain.Dto.Request;
    using System.Text;
    using Npgsql;
    using LocationService.Infrastructure.Repository.Specification;

    public class ProvinceAdapter : IProvincePort
    {
        private readonly LocationDbContext _dbContext;
        public ProvinceAdapter(LocationDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<ProvinceEntity> CreateAsync(ProvinceEntity province)
        {
            var provinceModel = ProvinceMapper.ToModel(province);
            _dbContext.Provinces.Add(provinceModel);
            await _dbContext.SaveChangesAsync();
            return ProvinceMapper.ToEntity(provinceModel);
        }

        public async Task<ProvinceEntity> GetByCodeAsync(string code)
        {
            var provinceModel = await _dbContext.Provinces
                .AsNoTracking()
                .FirstOrDefaultAsync(p => p.Code == code);
            return ProvinceMapper.ToEntity(provinceModel);
        }

        public async Task<List<ProvinceEntity>> GetProvinces(ProvinceParams provinceParams)
        {
            var (sql, parameters) = ProvinceSpecification.ToGetProvincesSpecification(provinceParams);

            var provinceModels = await _dbContext.Provinces
                .FromSqlRaw(sql.ToString(), parameters.ToArray())
                .AsNoTracking()
                .ToListAsync();

            return provinceModels.Select(ProvinceMapper.ToEntity).ToList();
        }

        public async Task<long> CountProvinces(ProvinceParams provinceParams)
        {
            var (sql, parameters) = ProvinceSpecification.ToCountProvincesSpecification(provinceParams);

            var result = await _dbContext.Database
                .SqlQueryRaw<long>(sql, parameters.ToArray())
                .ToListAsync();

            return result.FirstOrDefault();
        }
    }
}