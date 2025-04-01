using LocationService.Core.Domain.Entity;

namespace LocationService.Core.Port
{
    public interface ICategoryPort
    {
        Task<CategoryEntity> CreateCategoryAsync(CategoryEntity category);
        Task<CategoryEntity> GetCategoryByNameAsync(string name);
        Task<CategoryEntity> GetCategoryByIdAsync(long id);
    }
}