package huy.project.inventory_service.kernel.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.redis.lock")
@Configuration
@Getter
@Setter
public class RedisLockProperty {
    private String namespace;
    private Long expiration;
}
