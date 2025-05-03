package huy.project.accommodation_service.kernel.utils;

import huy.project.accommodation_service.core.domain.dto.request.AccommodationParams;

import java.util.stream.Collectors;

public class CacheUtils {
    public static final String CACHE_PREFIX_AMENITY_GROUP = "amenity_group::%d";
    public static final String CACHE_AMENITY_GROUP_LIST = "amenity_group_list";
    public static final String CACHE_PREFIX_ACCOMMODATION = "accommodation::%d";
    public static final String CACHE_ACCOMMODATION_LIST = "accommodation_list";

    public static String buildCacheKeyGetAmenityGroupById(Long amenityGroupId) {
        return String.format(CACHE_PREFIX_AMENITY_GROUP, amenityGroupId);
    }

    public static String buildCacheKeyGetAccommodationById(Long accommodationId) {
        return String.format(CACHE_PREFIX_ACCOMMODATION, accommodationId);
    }

    public static String buildCacheKeyGetAccommodations(AccommodationParams params) {
        StringBuilder key = new StringBuilder(CACHE_ACCOMMODATION_LIST);
        if (params.getIds() != null && !params.getIds().isEmpty()) {
            String ids = params.getIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            key.append("::ids::").append(ids);
        }
        if (params.getHostId() != null) {
            key.append("::host::").append(params.getHostId());
        }
        return key.toString();
    }
}