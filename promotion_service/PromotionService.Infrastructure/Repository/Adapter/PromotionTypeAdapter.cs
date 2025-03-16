using Microsoft.EntityFrameworkCore;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Domain.Port;
using PromotionService.Infrastructure.Repository.Mapper;

namespace PromotionService.Infrastructure.Repository.Adapter;

public class PromotionTypeAdapter : IPromotionTypePort
{
    private readonly PromotionDbContext _dbContext;
    
    public PromotionTypeAdapter(PromotionDbContext dbContext)
    {
        _dbContext = dbContext;
    }
    
    public async Task<PromotionTypeEntity> AddPromotionTypeAsync(PromotionTypeEntity promotionType)
    {
        var promotionTypeModel = PromotionTypeMapper.ToModel(promotionType);
        _dbContext.PromotionTypes.Add(promotionTypeModel);
        await _dbContext.SaveChangesAsync();
        return PromotionTypeMapper.ToEntity(promotionTypeModel);
    }

    public async Task<PromotionTypeEntity> GetPromotionTypeByNameAsync(string name)
    {
        var promotionType = await _dbContext.PromotionTypes.FirstOrDefaultAsync(x => x.Name == name);
        return PromotionTypeMapper.ToEntity(promotionType);
    }
}