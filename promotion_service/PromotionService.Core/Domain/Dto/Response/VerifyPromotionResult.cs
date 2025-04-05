namespace PromotionService.Core.Domain.Dto.Response
{
    public class VerifyPromotionResult
    {
        public long PromotionId { get; set; }
        public bool IsValid { get; set; }
        public string Message { get; set; }
    }

    public class VerifyPromotionResponse
    {
        public List<VerifyPromotionResult> Promotions { get; set; }
        public bool IsValid { get; set; }
    }
}