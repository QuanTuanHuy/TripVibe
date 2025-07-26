package huy.project.api_gateway.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServiceProperties {
    private Map<String, ServiceProperty> services = new HashMap<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceProperty {
        private String uri;
        private String name;
    }
}
