package huy.project.ai_service.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentModel {
    private String id;
    private String content;
    private String fileName;
    private String contentType;
    private Long size;
    private Instant uploadedAt;
    private Map<String, Object> metadata;
    private String status;
}
