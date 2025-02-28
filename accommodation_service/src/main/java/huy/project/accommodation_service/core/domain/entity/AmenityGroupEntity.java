package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AmenityGroupEntity {
    private Long id;
    private String name;
    private String description;
    private List<AmenityEntity> amenities;
}
