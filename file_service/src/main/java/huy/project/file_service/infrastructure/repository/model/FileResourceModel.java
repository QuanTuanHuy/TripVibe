package huy.project.file_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "file_resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResourceModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "original_name")
    private String originalName;
    @Column(name = "content_type")
    private String contentType;
    @Column(name = "size")
    private Long size;
    @Column(name = "url")
    private String url;
    @Column(name = "created_by")
    private Long createdBy;
}
