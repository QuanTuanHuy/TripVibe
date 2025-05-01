package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAmenityRequestDto {
    private String name;
    private String icon;
    private String description;
    private Boolean isPaid;
    private String availableTime;
    private Boolean isHighlighted;
    private Boolean isFilterable;
    private Long groupId;
}
