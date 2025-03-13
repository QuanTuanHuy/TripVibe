package huy.project.profile_service.kernel.utils;

public class CacheUtils {
    public static final String CACHE_PREFIX_TOURIST = "tourist::detail::%d";

    public static String buildCacheKeyGetTouristById(Long touristId) {
        return String.format(CACHE_PREFIX_TOURIST, touristId);
    }
}