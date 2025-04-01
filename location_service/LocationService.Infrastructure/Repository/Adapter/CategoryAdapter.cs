using LocationService.Core.Domain.Entity;
using LocationService.Core.Port;
using LocationService.Infrastructure.Repository.Mapper;
using Microsoft.EntityFrameworkCore;

namespace LocationService.Infrastructure.Repository.Adapter
{
    public class CategoryAdapter : ICategoryPort
    {
        private readonly LocationDbContext _dbContext;

        public CategoryAdapter(LocationDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        public async Task<CategoryEntity> CreateCategoryAsync(CategoryEntity category)
        {
            var model = CategoryMapper.ToModel(category);
            _dbContext.Categories.Add(model);
            await _dbContext.SaveChangesAsync();
            return CategoryMapper.ToEntity(model);
        }

        public async Task<CategoryEntity> GetCategoryByNameAsync(string name)
        {
            var model = await _dbContext.Categories.FirstOrDefaultAsync(c => c.Name == name);
            return CategoryMapper.ToEntity(model);
        }
    }
}