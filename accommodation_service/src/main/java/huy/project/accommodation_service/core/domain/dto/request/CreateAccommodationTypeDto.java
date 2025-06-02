package huy.project.accommodation_service.core.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccommodationTypeDto {
    private String name;
    private String description;
    private String imageUrl;
    private String iconUrl;
    private Boolean isHighlighted;
}
