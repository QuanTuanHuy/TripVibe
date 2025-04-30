package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AmenityEntity {
    private Long id;
    private String name;
    private String icon;
    private String description;
    private Boolean isPaid;
    private BigDecimal price;
    private String availableTime; // e.g., "24/7", "8:00-22:00"
    private Boolean isHighlighted;
    private Boolean isFilterable;
    private Long groupId;
}
