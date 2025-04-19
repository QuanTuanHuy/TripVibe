package huy.project.accommodation_service.kernel.config;

import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestTemplate;

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

    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(()
                -> new HttpMessageConverters(new RestTemplate().getMessageConverters())));
    }
}