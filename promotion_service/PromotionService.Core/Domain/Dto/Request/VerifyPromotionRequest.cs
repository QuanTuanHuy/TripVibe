namespace PromotionService.Core.Domain.Dto.Request
{
    public class VerifyPromotionRequest
    {
        public long AccommodationId { get; set; }
        public List<long> PromotionIds { get; set; }
    }
}