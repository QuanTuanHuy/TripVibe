using LocationService.Core.Domain.Constant;
using LocationService.Core.Domain.Entity;
using LocationService.Core.Exception;
using LocationService.Core.Port;

namespace LocationService.Core.Service
{
    public interface IGetCategoryUseCase
    {
        Task<CategoryEntity> GetCategoryByIdAsync(long id);
        Task<List<CategoryEntity>> GetCategoriesByIdsAsync(List<long> ids);
    }

    public class GetCategoryUseCase : IGetCategoryUseCase
    {
        private readonly ICategoryPort _categoryPort;

        public GetCategoryUseCase(ICategoryPort categoryPort)
        {
            _categoryPort = categoryPort;
        }

        public async Task<CategoryEntity> GetCategoryByIdAsync(long id)
        {
            var category = await _categoryPort.GetCategoryByIdAsync(id);
            if (category == null)
            {
                throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
            }
            return category;
        }

        public async Task<List<CategoryEntity>> GetCategoriesByIdsAsync(List<long> ids)
        {
            return await _categoryPort.GetCategoriesByIdsAsync(ids);
        }
    }
}