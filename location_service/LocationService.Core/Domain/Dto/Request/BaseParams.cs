namespace LocationService.Core.Domain.Dto.Request
{
    public class BaseParams
    {
        public int Page { get; set; }
        public int PageSize { get; set; }
        public string? SortBy { get; set; }
        public string? SortOrder { get; set; }
    }
}