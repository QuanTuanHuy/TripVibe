package huy.project.search_service.kernel.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.elasticsearch")
@Data
public class ElasticsearchProperty {
    private String url;
    private String username;
    private String password;
}
