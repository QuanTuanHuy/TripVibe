package huy.project.api_gateway.config;

import huy.project.api_gateway.property.ServiceProperties;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CloudGatewayConfig {
    @Value("${app.api-prefix}")
    private String apiPrefix;

    private final ServiceProperties serviceProperties;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        RouteLocatorBuilder.Builder routes = builder.routes();

        serviceProperties.getServices().forEach((service, serviceProperty) -> {
            String serviceName = serviceProperty.getName();
            String serviceUri = serviceProperty.getUri();

            log.info("Configuring route for service: {}: {}", serviceName, serviceUri);

            if (StringUtils.isEmpty(serviceUri)) {
                log.error("Service URI for {} is not configured. Skipping route creation.", serviceName);
            }
            log.info("Service URI for {} is configured. Creating route.", serviceName);
            routes.route(serviceName, route -> route
                    .path("/" + serviceName + "/" + apiPrefix + "/**")
                    .filters(f ->
                            f.retry(r -> r.setRetries(3)))
                    .uri(serviceUri)
            );
        });

        return routes.build();
    }
}
