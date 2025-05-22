package huy.project.api_gateway.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient
public class DiscoveryConfig {
    // Configuration is primarily done through application.yml
    // This class serves as a marker for enabling service discovery
}
