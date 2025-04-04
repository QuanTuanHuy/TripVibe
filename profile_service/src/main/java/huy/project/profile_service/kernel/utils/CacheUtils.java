package huy.project.profile_service.kernel.utils;

public class CacheUtils {
    public static final String CACHE_PREFIX_TOURIST = "profile_service::tourist::detail::%d";
    public static final String CACHE_PREFIX_USER_PROFILE = "profile_service:user_profile::%d";

    public static String buildCacheKeyGetTouristById(Long touristId) {
        return String.format(CACHE_PREFIX_TOURIST, touristId);
    }

    public static String buildCacheKeyGetUserProfileById(Long userId) {
        return String.format(CACHE_PREFIX_USER_PROFILE, userId);
    }
}