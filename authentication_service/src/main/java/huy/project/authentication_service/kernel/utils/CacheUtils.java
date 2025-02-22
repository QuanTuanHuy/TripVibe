package huy.project.authentication_service.kernel.utils;

public class CacheUtils {
    public static final String CACHE_PREFIX_ROLE = "role::%d";
    public static final String CACHE_PREFIX_USER = "user::%d";
    public static final String CACHE_PREFIX_USER_EMAIL = "user::email::%s";

    public static String buildCacheKeyGetRoleById(Long roleId) {
        return String.format(CACHE_PREFIX_ROLE, roleId);
    }

    public static String buildCacheKeyGetUserById(Long userId) {
        return String.format(CACHE_PREFIX_USER, userId);
    }

    public static String buildCacheKeyGetUserByEmail(String email) {
        return String.format(CACHE_PREFIX_USER_EMAIL, email);
    }
}