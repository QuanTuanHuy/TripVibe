namespace PromotionService.Infrastructure.Repository.Adapter
{
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Microsoft.EntityFrameworkCore;
    using PromotionService.Core.Domain.Entity;
    using PromotionService.Core.Port;
    using PromotionService.Infrastructure.Repository.Mapper;

    public class PromotionUnitAdapter : IPromotionUnitPort
    {
        private readonly PromotionDbContext _dbContext;

        public PromotionUnitAdapter(PromotionDbContext dbContext)
        {
            _dbContext = dbContext;
        }


        public async Task CreatePromotionUnitsAsync(List<PromotionUnitEntity> promotionUnits)
        {
            var promotionUnitModels = promotionUnits.Select(PromotionUnitMapper.MapToModel).ToList();
            await _dbContext.PromotionUnits.AddRangeAsync(promotionUnitModels);
            await _dbContext.SaveChangesAsync();
        }

        public async Task<List<PromotionUnitEntity>> GetPromotionUnitsByPromotionIdAsync(long promotionId)
        {
            var promotionUnitModels = await _dbContext.PromotionUnits
                .AsNoTracking()
                .Where(x => x.PromotionId == promotionId)
                .ToListAsync();
            return promotionUnitModels.Select(PromotionUnitMapper.ToEntity).ToList();
        }

        public async Task<List<PromotionUnitEntity>> GetPromotionUnitsByPromotionIdsAsync(List<long> promotionIds)
        {
            var promotionUnitModels = await _dbContext.PromotionUnits
                .AsNoTracking()
                .Where(x => promotionIds.Contains(x.PromotionId))
                .ToListAsync();
            return promotionUnitModels.Select(PromotionUnitMapper.ToEntity).ToList();
        }

        public async Task DeletePromotionUnitsByPromotionIdsAsync(List<long> promotionIds)
        {
            await _dbContext.PromotionUnits
                .Where(pu => promotionIds.Contains(pu.PromotionId))
                .ExecuteDeleteAsync();
            await _dbContext.SaveChangesAsync();
        }

        public async Task DeletePromotionUnitsByPromotionIdAsync(long promotionId)
        {
            await _dbContext.PromotionUnits
                .Where(pu => pu.PromotionId == promotionId)
                .ExecuteDeleteAsync();
            await _dbContext.SaveChangesAsync();
        }
    }
}