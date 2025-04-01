namespace LocationService.Infrastructure.Repository.Model
{
    using System.ComponentModel.DataAnnotations;
    using System.ComponentModel.DataAnnotations.Schema;

    [Table("attraction_schedules")]
    public class AttractionScheduleModel : AuditModel
    {
        [Key]
        [Column("id")]
        public long Id { get; set; }

        [Column("attraction_id")]
        public long AttractionId { get; set; }

        [Column("day_of_week")]
        public int DayOfWeek { get; set; }

        [Column("open_time")]
        public TimeSpan OpenTime { get; set; }

        [Column("close_time")]
        public TimeSpan CloseTime { get; set; }

        [Column("is_closed")]
        public bool IsClosed { get; set; }

        [Column("season_start")]
        public DateTime? SeasonStart { get; set; }

        [Column("season_end")]
        public DateTime? SeasonEnd { get; set; }
    }
}