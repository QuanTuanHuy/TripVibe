using Microsoft.EntityFrameworkCore;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Port;
using PromotionService.Infrastructure.Repository.Mapper;
using PromotionService.Infrastructure.Repository.Model;

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

    public async Task<PromotionEntity> GetPromotionByIdAsync(long id)
    {
        var promotionModel = await _dbContext.Promotions.AsNoTracking()
            .FirstOrDefaultAsync(p => p.Id == id);
        return PromotionMapper.ToEntity(promotionModel);
    }

    public async Task UpdatePromotionAsync(PromotionEntity promotion)
    {
        var promotionMode = PromotionMapper.ToModel(promotion);
        _dbContext.Promotions.Update(promotionMode);

        await _dbContext.SaveChangesAsync();
    }

    public async Task<(List<PromotionEntity>, int)> GetPromotionsAsync(PromotionParams queryParams)
    {
        var query = _dbContext.Promotions.AsNoTracking().AsQueryable();

        // Apply filters
        if (!string.IsNullOrEmpty(queryParams.Name))
        {
            query = query.Where(p => p.Name.Contains(queryParams.Name));
        }

        if (queryParams.PromotionTypeId.HasValue)
        {
            query = query.Where(p => p.TypeId == queryParams.PromotionTypeId);
        }

        if (queryParams.AccommodationId.HasValue)
        {
            query = query.Where(p => p.AccommodationId == queryParams.AccommodationId);
        }

        if (queryParams.IsActive.HasValue)
        {
            query = query.Where(p => p.IsActive == queryParams.IsActive);
        }

        // Filter for active promotions (current date between start and end dates)
        if (queryParams.StartDate.HasValue)
        {
            var startDate = DateTime.SpecifyKind(
                DateTimeOffset.FromUnixTimeMilliseconds(queryParams.StartDate.Value).DateTime,
                DateTimeKind.Utc);
            query = query.Where(p => p.EndDate >= startDate); // Promotions that end after the start date
        }

        if (queryParams.EndDate.HasValue)
        {
            var endDate = DateTime.SpecifyKind(
                DateTimeOffset.FromUnixTimeMilliseconds(queryParams.EndDate.Value).DateTime,
                DateTimeKind.Utc);
            query = query.Where(p => p.StartDate <= endDate); // Promotions that start before the end date
        }

        // Count total records (before pagination)
        var totalCount = await query.CountAsync();

        // Apply sorting
        query = ApplySorting(query, queryParams);

        // Apply pagination (with safe defaults)
        var page = Math.Max(0, queryParams.Page);
        var pageSize = queryParams.PageSize <= 0 ? 10 : queryParams.PageSize; 

        query = query
            .Skip(page * pageSize)
            .Take(pageSize);


        // Execute query and map results to entities
        var results = await query.ToListAsync();
        var promotions = results.Select(p => PromotionMapper.ToEntity(p)).ToList();

        return (promotions, totalCount);
    }

    private IQueryable<PromotionModel> ApplySorting(IQueryable<PromotionModel> query, PromotionParams queryParams)
    {
        if (string.IsNullOrEmpty(queryParams.SortBy))
        {
            return query.OrderByDescending(p => p.Id);
        }

        var sortOrder = queryParams.SortOrder?.ToLower() == "desc";

        switch (queryParams.SortBy.ToLower())
        {
            case "name":
                return sortOrder ? query.OrderByDescending(p => p.Name) : query.OrderBy(p => p.Name);
            default:
                return query.OrderByDescending(p => p.Id);
        }
    }

    public async Task DeletePromotionsByTypeIdAsync(long typeId) {
        await _dbContext.Promotions
            .Where(p => p.TypeId == typeId)
            .ExecuteDeleteAsync();
    }

    public async Task<List<PromotionEntity>> GetPromotionsByTypeIdAsync(long typeId) {
        var promotions = await _dbContext.Promotions
            .AsNoTracking()
            .Where(p => p.TypeId == typeId)
            .ToListAsync();

        return promotions.Select(PromotionMapper.ToEntity).ToList();
    }

    public async Task<List<PromotionEntity>> GetPromotionsByIds(List<long> ids) {
        var promotions = await _dbContext.Promotions
            .AsNoTracking()
            .Where(p => ids.Contains(p.Id))
            .ToListAsync();

        return promotions.Select(PromotionMapper.ToEntity).ToList();
    }
}