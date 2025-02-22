package huy.project.authentication_service.infrastructure.redis;

import huy.project.authentication_service.core.port.ICachePort;
import huy.project.authentication_service.kernel.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheAdapter implements ICachePort {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void setToCache(String key, Object value, Long ttl) {
        try {
            String valueStr = JsonUtils.toJson(value);
            redisTemplate.opsForValue().set(key, valueStr, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error when set to cache, err: {}", e.getMessage());
        }
    }

    @Override
    public Object getFromCache(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Error when get from cache, err: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public <T> T getFromCache(String key, Class<T> clazz) {
        try {
            String valueStr = redisTemplate.opsForValue().get(key);
            return JsonUtils.fromJson(valueStr, clazz);
        } catch (Exception e) {
            log.error("Error when get from cache, err: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteFromCache(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Error when delete from cache, err: {}", e.getMessage());
        }
    }
}
