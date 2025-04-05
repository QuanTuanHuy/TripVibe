namespace LocationService.Kernel.Utils;

public static class CacheUtils
{
    public const string CachePrefixAttraction = "location_service.attraction::{0}";

    public static string BuildCacheKeyGetAttractionById(long id) => 
        string.Format(CachePrefixAttraction, id);
}
