namespace LocationService.Core.Domain.Entity
{
    public class TrendingPlaceEntity
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public string Type { get; set; } // ATTRACTION, LOCATION, HOTEL, etc.
        public long ReferenceId { get; set; } // ID of the referenced entity (attraction, location, etc.)
        public int Rank { get; set; }
        public DateTime StartDate { get; set; }
        public DateTime EndDate { get; set; }
        public bool IsActive { get; set; }
        public string Tags { get; set; }
        public string ImageUrl { get; set; }
        public double Score { get; set; }
        public Dictionary<string, object> Metadata { get; set; }
        public DateTime CreatedAt { get; set; }
        public DateTime UpdatedAt { get; set; }
    }
}