package huy.project.inventory_service.kernel.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaServer {
    private String bootstrapServers;
}
