package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccommodationTypeEntity {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String iconUrl;
    private Boolean isHighlighted;
}
