using LocationService.Core.Domain.Dto.Request;
using LocationService.Core.Domain.Entity;
using LocationService.Core.UseCase;

namespace LocationService.Core.Service
{
    public interface ICategoryService
    {
        Task<CategoryEntity> CreateCategoryAsync(CreateCategoryDto req);
    }

    public class CategoryService : ICategoryService
    {
        private readonly ICreateCategoryUseCase _createCategoryUseCase;

        public CategoryService(ICreateCategoryUseCase createCategoryUseCase)
        {
            _createCategoryUseCase = createCategoryUseCase;
        }

        public async Task<CategoryEntity> CreateCategoryAsync(CreateCategoryDto req)
        {
            return await _createCategoryUseCase.CreateCategoryAsync(req);
        }
    }
}