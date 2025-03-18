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
}