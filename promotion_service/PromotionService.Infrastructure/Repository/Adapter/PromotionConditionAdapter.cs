using Microsoft.EntityFrameworkCore;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Port;
using PromotionService.Infrastructure.Repository.Mapper;

namespace PromotionService.Infrastructure.Repository.Adapter;

public class PromotionConditionAdapter : IPromotionConditionPort
{
    private readonly PromotionDbContext _dbContext;

    public PromotionConditionAdapter(PromotionDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    public async Task<List<PromotionConditionEntity>> CreatePromotionConditionsAsync(List<PromotionConditionEntity> promotionConditions)
    {
        var promotionConditionModels = promotionConditions.Select(PromotionConditionMapper.ToModel).ToList();
        await _dbContext.PromotionConditions.AddRangeAsync(promotionConditionModels);
        await _dbContext.SaveChangesAsync();

        return promotionConditionModels.Select(PromotionConditionMapper.ToEntity).ToList();
    }

    public async Task<List<PromotionConditionEntity>> GetPromotionConditionsByPromotionIdAsync(long promotionId)
    {
        var promotionConditions = await _dbContext.PromotionConditions
            .AsNoTracking()
            .Where(pc => pc.PromotionId == promotionId)
            .ToListAsync();

        return promotionConditions
            .Select(PromotionConditionMapper.ToEntity)
            .ToList();
    }

    public async Task<List<PromotionConditionEntity>> GetPromotionConditionsByPromotionIdsAsync(List<long> promotionIds)
    {
        var promotionConditions = await _dbContext.PromotionConditions
            .AsNoTracking()
            .Where(pc => promotionIds.Contains(pc.PromotionId))
            .ToListAsync();

        return promotionConditions
            .Select(PromotionConditionMapper.ToEntity)
            .ToList();
    }

    public async Task DeletePromotionConditionsByPromotionIdsAsync(List<long> promotionIds)
    {
        await _dbContext.PromotionConditions
            .Where(pc => promotionIds.Contains(pc.PromotionId))
            .ExecuteDeleteAsync();
        await _dbContext.SaveChangesAsync();
    }

    public async Task DeletePromotionConditionsByPromotionIdAsync(long promotionId)
    {
        await _dbContext.PromotionConditions
            .Where(pc => pc.PromotionId == promotionId)
            .ExecuteDeleteAsync();
        await _dbContext.SaveChangesAsync();
    }
}