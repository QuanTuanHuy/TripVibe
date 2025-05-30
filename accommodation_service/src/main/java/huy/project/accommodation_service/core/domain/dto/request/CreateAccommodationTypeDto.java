package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccommodationTypeDto {
    private String name;
    private String description;
    private String imageUrl;
    private String iconUrl;
    private Boolean isHighlighted;
}
