namespace LocationService.Infrastructure.Repository.Mapper
{
    using LocationService.Core.Domain.Entity;
    using LocationService.Infrastructure.Repository.Model;

    public class AttractionScheduleMapper
    {
        public static AttractionScheduleEntity ToEntity(AttractionScheduleModel? model)
        {
            if (model == null)
                return null;

            return new AttractionScheduleEntity
            {
                Id = model.Id,
                AttractionId = model.AttractionId,
                DayOfWeek = model.DayOfWeek,
                OpenTime = model.OpenTime,
                CloseTime = model.CloseTime,
                IsClosed = model.IsClosed,
                SeasonStart = model.SeasonStart,
                SeasonEnd = model.SeasonEnd
            };
        }

        public static AttractionScheduleModel ToModel(AttractionScheduleEntity? entity)
        {
            if (entity == null)
                return null;

            return new AttractionScheduleModel
            {
                Id = entity.Id,
                AttractionId = entity.AttractionId,
                DayOfWeek = entity.DayOfWeek,
                OpenTime = entity.OpenTime,
                CloseTime = entity.CloseTime,
                IsClosed = entity.IsClosed,
                SeasonStart = entity.SeasonStart,
                SeasonEnd = entity.SeasonEnd
            };
        }
    }
}