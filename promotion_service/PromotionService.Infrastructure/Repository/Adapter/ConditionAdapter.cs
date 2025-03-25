using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Port;
using PromotionService.Infrastructure.Repository.Mapper;

namespace PromotionService.Infrastructure.Repository.Adapter;

public class ConditionAdapter : IConditionPort
{
    private readonly PromotionDbContext _dbContext;
    private readonly ILogger<ConditionAdapter> _logger;

    public ConditionAdapter(PromotionDbContext dbContext, ILogger<ConditionAdapter> logger)
    {
        _dbContext = dbContext;
        _logger = logger;
    }

    public async Task<ConditionEntity> CreateConditionAsync(ConditionEntity condition)
    {
        try
        {
            var conditionModel = ConditionMapper.ToModel(condition);
            var result = await _dbContext.Conditions.AddAsync(conditionModel);
            await _dbContext.SaveChangesAsync();
            return ConditionMapper.ToEntity(result.Entity);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error creating condition: {Message}", ex.Message);
            throw;
        }
    }

    public async Task<ConditionEntity> GetConditionByNameAsync(string name)
    {
        var condition = await _dbContext.Conditions
            .AsNoTracking()
            .FirstOrDefaultAsync(c => c.Name == name);
        return ConditionMapper.ToEntity(condition);
    }

    public async Task<List<ConditionEntity>> GetConditionsByIdsAsync(List<long> ids)
    {
        return await _dbContext.Conditions
            .AsNoTracking()
            .Where(c => ids.Contains(c.Id))
            .Select(c => ConditionMapper.ToEntity(c)).ToListAsync();
    }
}