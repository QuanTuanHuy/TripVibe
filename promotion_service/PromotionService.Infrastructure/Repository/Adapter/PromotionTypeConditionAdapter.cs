using Microsoft.EntityFrameworkCore;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Port;
using PromotionService.Infrastructure.Repository.Mapper;

namespace PromotionService.Infrastructure.Repository.Adapter
{
    public class PromotionTypeConditionAdapter : IPromotionTypeConditionPort
    {
        private readonly PromotionDbContext _dbContext;
        public PromotionTypeConditionAdapter(PromotionDbContext dbContext)
        {
            _dbContext = dbContext;
        }
        public async Task<List<PromotionTypeConditionEntity>> CreatePromotionTypeConditionsAsync(List<PromotionTypeConditionEntity> promotionTypeConditions)
        {
            var ptConditionModels = promotionTypeConditions.Select(PromotionTypeConditionMapper.ToModel).ToList();
            await _dbContext.PromotionTypeConditions.AddRangeAsync(ptConditionModels);
            await _dbContext.SaveChangesAsync();
            return ptConditionModels.Select(PromotionTypeConditionMapper.ToEntity).ToList();
        }

        public async Task<List<PromotionTypeConditionEntity>> GetPromotionTypeConditionsByPromotionTypeIdAsync(long promotionTypeId)
        {
            var ptConditions = await _dbContext.PromotionTypeConditions
                .AsNoTracking()
                .Where(ptc => ptc.PromotionTypeId == promotionTypeId)
                .ToListAsync();
            return ptConditions.Select(PromotionTypeConditionMapper.ToEntity).ToList();
        }
    }
}