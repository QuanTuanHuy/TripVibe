using Microsoft.EntityFrameworkCore;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Domain.Port;
using PromotionService.Infrastructure.Repository.Mapper;
using PromotionService.Infrastructure.Repository.Model;

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

    public async Task<List<PromotionTypeEntity>> GetPromotionTypesAsync(PromotionTypeParams queryParams)
    {
        var query = ApplyFiltersAndSorting(_dbContext.PromotionTypes.AsQueryable(), queryParams);

        // Apply pagination
        query = query.Skip(queryParams.Page * queryParams.PageSize)
            .Take(queryParams.PageSize);

        var promotionTypes = await query.ToListAsync();
        return promotionTypes.Select(PromotionTypeMapper.ToEntity).ToList();
    }

    public async Task<int> CountPromotionTypesAsync(PromotionTypeParams queryParams)
    {
        var query = ApplyFilters(_dbContext.PromotionTypes.AsQueryable(), queryParams);
        return await query.CountAsync();
    }
    
    public async Task<(List<PromotionTypeEntity> Items, int TotalCount)> GetPromotionTypesWithCountAsync(PromotionTypeParams queryParams)
    {
        var items = await GetPromotionTypesAsync(queryParams);
        var totalCount = await CountPromotionTypesAsync(queryParams);
        
        return (items, totalCount);
    }

    private IQueryable<PromotionTypeModel> ApplyFilters(IQueryable<PromotionTypeModel> query, PromotionTypeParams queryParams)
    {
        // Apply filters
        if (!string.IsNullOrEmpty(queryParams.Name))
        {
            query = query.Where(pt => pt.Name.Contains(queryParams.Name));
        }
        
        return query;
    }
    
    private IQueryable<PromotionTypeModel> ApplyFiltersAndSorting(IQueryable<PromotionTypeModel> query, PromotionTypeParams queryParams)
    {
        // Apply filters first
        query = ApplyFilters(query, queryParams);
        
        // Apply sorting
        if (!string.IsNullOrEmpty(queryParams.SortBy))
        {
            switch (queryParams.SortBy.ToLower())
            {
                case "name":
                    query = queryParams.SortOrder?.ToLower() == "desc" 
                        ? query.OrderByDescending(pt => pt.Name)
                        : query.OrderBy(pt => pt.Name);
                    break;
                default:
                    query = query.OrderBy(pt => pt.Id);
                    break;
            }
        }
        else
        {
            query = query.OrderBy(pt => pt.Id);
        }
        
        return query;
    }
}