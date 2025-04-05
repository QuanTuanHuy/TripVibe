namespace PromotionService.Core.Domain.Dto.Request;

public class BaseParams
{
    public int Page { get; set; } = 0;
    public int PageSize { get; set; } = 10;
    public string SortBy { get; set; } = "Id";
    public string SortOrder { get; set; } = "asc";
}