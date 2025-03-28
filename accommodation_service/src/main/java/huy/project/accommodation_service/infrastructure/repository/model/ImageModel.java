package huy.project.accommodation_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "images")
public class ImageModel extends AuditTable {
    @Id
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "is_primary")
    private Boolean isPrimary;
}
