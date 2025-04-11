namespace LocationService.Core.Domain.Dto.Request
{
    public class CreateTrendingPlaceDto
    {
        public string Type { get; set; } // ATTRACTION, LOCATION, HOTEL, etc.
        public long ReferenceId { get; set; } // ID of the referenced entity (attraction, location, etc.)
        public int Rank { get; set; }
        public double Score { get; set; }
        public string ImageUrl { get; set; }
        public Dictionary<string, object> Metadata { get; set; }

    }
}