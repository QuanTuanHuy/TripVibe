package huy.project.ai_service.core.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import huy.project.ai_service.core.domain.constant.DocumentStatus;
import jakarta.persistence.*;
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
@Entity(name = "documents")
public class DocumentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "size")
    private Long size;

    @Column(name = "uploaded_at")
    private Instant uploadedAt;

    @Column(name = "status")
    private DocumentStatus status;

    @Column(name = "file_path", nullable = false)
    private String filePath;
}
