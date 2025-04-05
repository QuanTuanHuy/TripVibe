using LocationService.Core.Domain.Entity;

namespace LocationService.Core.Domain.Dto.Request
{
    public class CreateAttractionDto
    {
        public string Name { get; set; }
        public string Description { get; set; }
        public long CategoryId { get; set; }
        public List<long> languageIds { get; set; }
        public CreateLocationDto Location { get; set; }
        public List<CreateImageDto> Images { get; set; }
        public List<CreateScheduleDto> Schedules { get; set; }

        public AttractionEntity ToEntity()
        {
            return new AttractionEntity
            {
                Name = Name,
                Description = Description,
                CategoryId = CategoryId,
            };
        }
    }

    public class CreateImageDto
    {
        public string Url { get; set; } = string.Empty;
        public bool IsPrimary { get; set; }
    }

    public class CreateScheduleDto
    {
        public int DayOfWeek { get; set; }
        public TimeSpan OpenTime { get; set; }
        public TimeSpan CloseTime { get; set; }
        public bool IsClosed { get; set; }
        public DateTime? SeasonStart { get; set; }
        public DateTime? SeasonEnd { get; set; }

        public AttractionScheduleEntity ToEntity()
        {
            return new AttractionScheduleEntity
            {
                DayOfWeek = DayOfWeek,
                OpenTime = OpenTime,
                CloseTime = CloseTime,
                IsClosed = IsClosed,
                SeasonStart = SeasonStart,
                SeasonEnd = SeasonEnd
            };
        }
    }
}