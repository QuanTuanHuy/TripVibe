package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAmenityRequestDto {
    private String name;
    private String description;
    private Long groupId;
}
