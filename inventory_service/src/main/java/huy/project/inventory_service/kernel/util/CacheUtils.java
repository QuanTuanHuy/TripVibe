package huy.project.inventory_service.kernel.util;

public class CacheUtils {
    public static final String NAME_SPACE = "inventory_service:";
    public static final String CACHE_PREFIX_ACCOMMODATION = NAME_SPACE + "accommodation:%d:%s";

    public static String buildCacheKeyGetAccById(Long id, boolean includeUnits) {
        return String.format(CACHE_PREFIX_ACCOMMODATION, id, includeUnits ? "include_units" : "exclude_units");
    }
}