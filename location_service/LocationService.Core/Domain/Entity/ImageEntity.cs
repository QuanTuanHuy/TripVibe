namespace LocationService.Core.Domain.Entity
{
    public class ImageEntity
    {
        public long Id { get; set; }
        public long entityId { get; set; }
        public string Url { get; set; }
        public string EntityType { get; set; }
        public bool IsPrimary { get; set; }
    }
}