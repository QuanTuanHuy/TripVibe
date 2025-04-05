using PromotionService.Core.Domain.Dto.Response;

public class PagedResponse<T>
{
    public IEnumerable<T> Data { get; set; }
    public PageInfo PageInfo { get; set; }
}