namespace LocationService.Infrastructure.Repository.Mapper
{
    using LocationService.Core.Domain.Entity;
    using LocationService.Infrastructure.Repository.Model;

    public class ImageMapper
    {
        public static ImageEntity ToEntity(ImageModel model)
        {
            if (model == null)
                return null;

            return new ImageEntity
            {
                Id = model.Id,
                entityId = model.EntityId,
                Url = model.Url,
                EntityType = model.EntityType,
                IsPrimary = model.IsPrimary
            };
        }

        public static ImageModel ToModel(ImageEntity entity)
        {
            if (entity == null)
                return null;

            return new ImageModel
            {
                Id = entity.Id,
                EntityId = entity.entityId,
                Url = entity.Url,
                EntityType = entity.EntityType,
                IsPrimary = entity.IsPrimary
            };
        }
    }
}