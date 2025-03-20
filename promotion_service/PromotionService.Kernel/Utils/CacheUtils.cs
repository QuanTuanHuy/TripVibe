namespace PromotionService.Kernel.Utils;

public static class CacheUtils
{
    public const string CachePrefixPromotion = "promotion_service.promotion::{0}";

    public static string BuildCacheKeyGetPromotionById(long promotionId) => 
        string.Format(CachePrefixPromotion, promotionId);
    
}