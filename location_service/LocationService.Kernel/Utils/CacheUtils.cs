namespace LocationService.Kernel.Utils;

public static class CacheUtils
{
    public const string CachePrefixAttraction = "location_service.attraction::{0}";
    public const string CachePrefixTrendingPlaceByType = "location_service.trending_places:type::{0}:{1}:{2}";
    public const string CachePrefixTrendingPlaceById = "location_service.trending_place::{0}";
    public const string CachePrefixTrendingPlaces = "location_service.trending_places:all::{0}:{1}";

    public static string BuildCacheKeyGetAttractionById(long id) => 
        string.Format(CachePrefixAttraction, id);

    public static string BuildCacheKeyGetTrendingPlacesByType(string type, int page, int size) =>
        string.Format(CachePrefixTrendingPlaceByType, type, page, size);

    public static string BuildCacheKeyGetTrendingPlaceById(long id) =>
        string.Format(CachePrefixTrendingPlaceById, id);

    public static string BuildCacheKeyGetTrendingPlaces(int page, int size) =>
        string.Format(CachePrefixTrendingPlaces, page, size);
}
