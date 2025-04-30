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
    private String type; // PROPERTY, ACCOMMODATION, UNIT, BATHROOM, etc.
    private String description;
    private String icon;
    private Integer displayOrder;
    private Boolean isPopular;
    private List<AmenityEntity> amenities;
}
