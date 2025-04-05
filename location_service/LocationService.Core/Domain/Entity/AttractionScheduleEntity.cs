namespace LocationService.Core.Domain.Entity
{
    public class AttractionScheduleEntity
    {
        public long Id { get; set; }
        public long AttractionId { get; set; }
        public int DayOfWeek { get; set; }
        public TimeSpan OpenTime { get; set; }
        public TimeSpan CloseTime { get; set; }
        public bool IsClosed { get; set; }
        public DateTime? SeasonStart { get; set; }
        public DateTime? SeasonEnd { get; set; }
    }
}