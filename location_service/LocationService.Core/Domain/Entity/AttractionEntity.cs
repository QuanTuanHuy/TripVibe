namespace LocationService.Core.Domain.Entity
{
    public class AttractionEntity
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public long LocationId { get; set; }
        public long CategoryId { get; set; }
        public LocationEntity Location { get; set; }
        public CategoryEntity Category { get; set; }
        public List<ImageEntity> Images { get; set; }
        public List<AttractionScheduleEntity> Schedules { get; set; }
        public List<LanguageEntity> Languages { get; set; }
    }
}