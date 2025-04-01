namespace LocationService.Infrastructure.Repository.Mapper
{
    using LocationService.Core.Domain.Entity;
    using LocationService.Infrastructure.Repository.Model;

    public static class AttractionLanguageMapper
    {
        public static AttractionLanguageEntity ToEntity(AttractionLanguageModel model)
        {
            if (model == null) return null;
            
            return new AttractionLanguageEntity
            {
                Id = model.Id,
                AttractionId = model.AttractionId,
                LanguageId = model.LanguageId
            };
        }

        public static AttractionLanguageModel ToModel(AttractionLanguageEntity entity)
        {
            if (entity == null) return null;
            
            return new AttractionLanguageModel
            {
                Id = entity.Id,
                AttractionId = entity.AttractionId,
                LanguageId = entity.LanguageId
            };
        }
    }
}