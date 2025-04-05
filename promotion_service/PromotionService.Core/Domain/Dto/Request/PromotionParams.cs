namespace PromotionService.Core.Domain.Dto.Request;

public class PromotionParams : BaseParams
{
    public long? AccommodationId { get; set; }
    public long? PromotionTypeId { get; set; }
    public string? Name { get; set; }
    public bool? IsActive { get; set; }
    public long? StartDate { get; set; }
    public long? EndDate { get; set; }
}