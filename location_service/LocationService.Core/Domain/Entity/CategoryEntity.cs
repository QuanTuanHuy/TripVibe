namespace LocationService.Core.Domain.Entity
{
    public class CategoryEntity
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public string IconUrl { get; set; }
        public bool IsActive { get; set; }
    }
}