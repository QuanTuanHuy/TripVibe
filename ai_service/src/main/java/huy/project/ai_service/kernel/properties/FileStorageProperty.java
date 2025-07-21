package huy.project.ai_service.kernel.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "app.file-storage")
public class FileStorageProperty {
    private String uploadDir;
    private List<String> allowedExtensions;
    private long maxFileSize;
}
