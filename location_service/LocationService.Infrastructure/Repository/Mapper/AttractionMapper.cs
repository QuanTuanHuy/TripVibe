namespace LocationService.Infrastructure.Repository.Mapper
{
    using LocationService.Core.Domain.Entity;
    using LocationService.Infrastructure.Repository.Model;

    public class AttractionMapper
    {
        public static AttractionEntity ToEntity(AttractionModel model)
        {
            if (model == null)
                return null;

            return new AttractionEntity
            {
                Id = model.Id,
                Name = model.Name,
                Description = model.Description,
                LocationId = model.LocationId,
                CategoryId = model.CategoryId,
            };
        }

        public static AttractionModel ToModel(AttractionEntity entity)
        {
            if (entity == null)
                return null;

            return new AttractionModel
            {
                Id = entity.Id,
                Name = entity.Name,
                Description = entity.Description,
                LocationId = entity.LocationId,
                CategoryId = entity.CategoryId
            };
        }
    }
}