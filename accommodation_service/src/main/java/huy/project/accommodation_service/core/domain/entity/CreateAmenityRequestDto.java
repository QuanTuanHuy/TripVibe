package huy.project.accommodation_service.core.domain.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAmenityRequestDto {
    private String name;
    private String description;
    private Long groupId;
}
