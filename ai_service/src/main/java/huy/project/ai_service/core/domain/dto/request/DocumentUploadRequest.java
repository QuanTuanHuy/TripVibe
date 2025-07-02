package huy.project.ai_service.core.domain.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Data
public class DocumentUploadRequest {
    private MultipartFile file;
    private String title;
    private Map<String, Object> metadata;
}
