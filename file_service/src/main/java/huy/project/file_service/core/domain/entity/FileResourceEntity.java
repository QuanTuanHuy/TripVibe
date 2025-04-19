package huy.project.file_service.core.domain.entity;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FileResourceEntity {
    private Long id;
    private String fileName;
    private String originalName;
    private String contentType;
    private Long size;
    private String url;
    private Long createdAt;
    private Long createdBy;

    private Boolean isImage;
    private Integer width;
    private Integer height;
    private Map<String, String> versions = new HashMap<>();

    private String category;
    private String referenceId;
    private String referenceType; // Accommodation, Unit, User
    private String tags;
    private String description;
    private Boolean isPublic;
    private String groupId;
}
