package huy.project.rating_service.kernel.utils;

public class CacheUtils {
    public static final String CACHE_PREFIX_ACCOMMODATION = "rating_service::accommodation::%d";
    public static final String CACHE_PREFIX_RATING_STATISTIC_BY_ACC_ID = "rating_service::rating_statistic::%d";
    public static final String CACHE_PREFIX_RATING_SUMMARY_BY_ACC_ID = "rating_service::rating_summary::%d";
    public static final String CACHE_PREFIX_USER_PROFILE = "rating_service::user_profile::%d";
    public static final String CACHE_PREFIX_UNIT_BY_ID = "rating_service::unit::%d";

    public static String buildCacheKeyGetAccommodationById(Long accId) {
        return String.format(CACHE_PREFIX_ACCOMMODATION, accId);
    }

    public static String buildCacheKeyGetRatingStatisticByAccId(Long accId) {
        return String.format(CACHE_PREFIX_RATING_STATISTIC_BY_ACC_ID, accId);
    }

    public static String buildCacheKeyGetRatingSummaryByAccId(Long accId) {
        return String.format(CACHE_PREFIX_RATING_SUMMARY_BY_ACC_ID, accId);
    }

    public static String buildCacheKeyGetUserProfileById(Long userId) {
        return String.format(CACHE_PREFIX_USER_PROFILE, userId);
    }

    public static String buildCacheKeyGetUnitById(Long unitId) {
        return String.format(CACHE_PREFIX_UNIT_BY_ID, unitId);
    }
}