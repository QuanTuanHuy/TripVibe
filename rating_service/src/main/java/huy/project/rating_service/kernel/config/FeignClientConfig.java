package huy.project.rating_service.kernel.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@Configuration
public class FeignClientConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials();
            String token = jwt.getTokenValue();

            requestTemplate.header("Authorization", "Bearer " + token);
        };
    }
}