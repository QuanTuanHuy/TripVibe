package huy.project.accommodation_service.core.port;

public interface ICachePort {
    void setToCache(String key, Object value, Long ttl);
    String getFromCache(String key);
    <T> T getFromCache(String key, Class<T> clazz);
    void deleteFromCache(String key);
    void deleteKeysByPattern(String pattern);
}
