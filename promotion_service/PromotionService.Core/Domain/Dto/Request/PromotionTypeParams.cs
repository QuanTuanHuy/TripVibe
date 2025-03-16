namespace PromotionService.Core.Domain.Dto.Request;

public class PromotionTypeParams : BaseParams
{
    // public PromotionTypeParams(int page, int pageSize, string sortBy, string sortOrder, string name)
    // {
    //     Page = page;
    //     PageSize = pageSize;
    //     SortBy = sortBy;
    //     SortOrder = sortOrder;
    //     Name = name;
    // }
    
    public string? Name { get; set; }
}