package huy.project.inventory_service.kernel.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@Configuration
@Slf4j
public class FeignClientConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            try {
                Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials();
                String token = jwt.getTokenValue();
                requestTemplate.header("Authorization", "Bearer " + token);
            } catch (Exception e) {
                log.info("Error getting JWT token: {}", e.getMessage());
            }
        };
    }
}