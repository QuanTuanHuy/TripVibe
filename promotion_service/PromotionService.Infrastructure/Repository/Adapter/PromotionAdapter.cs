using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Port;
using PromotionService.Infrastructure.Repository.Mapper;

namespace PromotionService.Infrastructure.Repository.Adapter;

public class PromotionAdapter : IPromotionPort
{
    private readonly PromotionDbContext _dbContext;
    
    public PromotionAdapter(PromotionDbContext dbContext)
    {
        _dbContext = dbContext;
    }
    
    public async Task<PromotionEntity> CreatePromotionAsync(PromotionEntity promotion)
    {
        var promotionModel = PromotionMapper.ToModel(promotion);
        var result = await _dbContext.Promotions.AddAsync(promotionModel);
        await _dbContext.SaveChangesAsync();
        return PromotionMapper.ToEntity(result.Entity);
    }
}