package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAmenityRequestDto {
    private String name;
    private String icon;
    private String description;
    private Boolean isPaid;
    private String availableTime; // e.g., "24/7", "8:00-22:00"
    private Boolean isHighlighted;
    private Boolean isFilterable;
    private Long groupId;
}
