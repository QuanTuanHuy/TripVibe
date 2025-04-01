namespace LocationService.Infrastructure.Repository.Mapper
{
    using LocationService.Core.Domain.Entity;
    using LocationService.Infrastructure.Repository.Model;

    public class LanguageMapper
    {
        public static LanguageEntity ToEntity(LanguageModel model)
        {
            if (model == null) return null!;
            return new LanguageEntity
            {
                Id = model.Id,
                Name = model.Name,
                Code = model.Code
            };
        }

        public static LanguageModel ToModel(LanguageEntity entity)
        {
            if (entity == null) return null!;
            return new LanguageModel
            {
                Id = entity.Id,
                Name = entity.Name,
                Code = entity.Code
            };
        }
    }
}