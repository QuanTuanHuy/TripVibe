namespace LocationService.Infrastructure.Repository.Mapper
{
    using LocationService.Core.Domain.Entity;
    using LocationService.Infrastructure.Repository.Model;

    public class CategoryMapper
    {
        public static CategoryModel ToModel(CategoryEntity entity)
        {
            if (entity == null) return null!;
            return new CategoryModel
            {
                Id = entity.Id,
                Name = entity.Name,
                Description = entity.Description,
                IconUrl = entity.IconUrl,
                IsActive = entity.IsActive
            };
        }

        public static CategoryEntity ToEntity(CategoryModel model)
        {
            if (model == null) return null!;
            return new CategoryEntity
            {
                Id = model.Id,
                Name = model.Name,
                Description = model.Description,
                IconUrl = model.IconUrl,
                IsActive = model.IsActive
            };
        }
    }
}