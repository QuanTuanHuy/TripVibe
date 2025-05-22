package huy.project.api_gateway.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebFilterConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedHeader("*");

        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:3001");

        config.addAllowedMethod("*");

        // Cho phép gửi credentials (cookies, authorization headers, etc.)
        config.setAllowCredentials(true);

        // Thời gian cache preflight request (OPTIONS)
        config.setMaxAge(3600L);

        // Áp dụng cho tất cả các path
        source.registerCorsConfiguration("/**", config);

        // Tạo filter
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));

        // Đặt filter này với độ ưu tiên cao nhất
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return bean;
    }
}