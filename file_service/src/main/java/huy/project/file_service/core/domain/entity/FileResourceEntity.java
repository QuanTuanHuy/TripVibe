package huy.project.file_service.core.domain.entity;

import lombok.*;

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
}
