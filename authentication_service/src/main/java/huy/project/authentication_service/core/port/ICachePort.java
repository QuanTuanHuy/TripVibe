package huy.project.authentication_service.core.port;

public interface ICachePort {
    void setToCache(String key, Object value, Long ttl);
    Object getFromCache(String key);
    <T> T getFromCache(String key, Class<T> clazz);
    void deleteFromCache(String key);
}
