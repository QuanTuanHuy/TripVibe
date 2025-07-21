package huy.project.ai_service.kernel.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "rag.memory")
public class ChatMemoryProperty {
    private Integer maxMessages;
    private Integer retentionDays;
    private Integer cleanupIntervalMs;
}
