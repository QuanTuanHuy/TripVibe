using LocationService.Core.Domain.Constant;
using LocationService.Core.Domain.Dto.Request;
using LocationService.Core.Domain.Entity;
using LocationService.Core.Exception;
using LocationService.Core.Port;

namespace LocationService.Core.UseCase
{
    public interface ICreateCategoryUseCase
    {
        Task<CategoryEntity> CreateCategoryAsync(CreateCategoryDto req);
    }

    public class CreateCategoryUseCase : ICreateCategoryUseCase
    {
        private readonly ICategoryPort _categoryPort;
        private readonly IDbTransactionPort _dbTransactionPort;

        public CreateCategoryUseCase(ICategoryPort categoryPort, IDbTransactionPort dbTransactionPort)
        {
            _categoryPort = categoryPort;
            _dbTransactionPort = dbTransactionPort;
        }

        public async Task<CategoryEntity> CreateCategoryAsync(CreateCategoryDto req)
        {
            var existedCategory = await _categoryPort.GetCategoryByNameAsync(req.Name);
            if (existedCategory != null)
            {
                throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
            }

            var category = req.ToEntity();
            category.IsActive = true;
            await _dbTransactionPort.ExecuteInTransactionAsync(async () =>
            {
                category = await _categoryPort.CreateCategoryAsync(category);
            });

            return category;
        }
    }
}