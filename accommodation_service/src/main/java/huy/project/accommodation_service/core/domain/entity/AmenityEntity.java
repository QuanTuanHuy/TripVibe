package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AmenityEntity {
    private Long id;
    private String name;
    private String description;
    private Long groupId;
}
