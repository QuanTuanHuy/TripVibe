package huy.project.accommodation_service.kernel.utils;

public class CacheUtils {
    public static final String CACHE_PREFIX_AMENITY_GROUP = "amenity_group::%d";
    public static final String CACHE_PREFIX_AMENITY_GROUP_LIST = "amenity_group_list";

    public static String buildCacheKeyGetAmenityGroupById(Long amenityGroupId) {
        return String.format(CACHE_PREFIX_AMENITY_GROUP, amenityGroupId);
    }
}