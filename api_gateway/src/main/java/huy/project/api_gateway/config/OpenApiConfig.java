package huy.project.api_gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port}")
    private String serverPort;

    @Bean
    public OpenAPI apiGatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Booking System API")
                        .description("API Gateway documentation for the Booking System microservices")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Developer Team")
                                .email("contact@example.com")
                                .url("https://yourdomain.com"))
                        .license(new License()
                                .name("API License")
                                .url("https://yourdomain.com/license")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Development Server"),
                        new Server().url("https://api.yourdomain.com").description("Production Server")
                ));
    }
}
