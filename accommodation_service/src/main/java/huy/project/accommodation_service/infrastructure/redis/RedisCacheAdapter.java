package huy.project.accommodation_service.infrastructure.redis;

import huy.project.accommodation_service.core.port.ICachePort;
import huy.project.accommodation_service.kernel.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheAdapter implements ICachePort {
    private final RedisTemplate<String, String> redisTemplate;

    private final JsonUtils jsonUtils;

    @Override
    public void setToCache(String key, Object value, Long ttl) {
        try {
            String valueStr = jsonUtils.toJson(value);
            log.info("valueStr: {}", valueStr);
            redisTemplate.opsForValue().set(key, valueStr, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error when set to cache, err: {}", e.getMessage());
        }
    }

    @Override
    public String getFromCache(String key) {
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
            return jsonUtils.fromJson(valueStr, clazz);
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

    @Override
    public void deleteKeysByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Deleted keys by pattern: {}", pattern);
            }
        } catch (Exception e) {
            log.error("Error when delete keys by pattern, err: {}", e.getMessage());
        }
    }
}

