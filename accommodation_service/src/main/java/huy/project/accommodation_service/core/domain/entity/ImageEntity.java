package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ImageEntity {
    private Long id;
    private String url;
    private Long entityId;
    private String entityType;
    private Boolean isPrimary;
}
