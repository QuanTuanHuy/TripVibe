package huy.project.profile_service.core.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResourceResponse {
    private Long id;
    private String fileName;
    private String originalName;
    private String contentType;
    private String url;
    private Long createdAt;
    private Long createdBy;
    private Map<String, String> versions = new HashMap<>();

    private String referenceId;
    private String referenceType;
}
