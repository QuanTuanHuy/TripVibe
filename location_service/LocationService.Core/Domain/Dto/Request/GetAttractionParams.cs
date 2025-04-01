namespace LocationService.Core.Domain.Dto.Request
{
    public class GetAttractionParams : BaseParams
    {
        public string? Name { get; set; }
        public long? CategoryId { get; set; }
        public long? CountryId { get; set; }
        public long? ProvinceId { get; set; }
        public List<long> LanguageIds { get; set; }
        public bool MatchAllLanguages { get; set; }
        public DateTime? FromDate { get; set; }
        public DateTime? ToDate { get; set; }
    }
}