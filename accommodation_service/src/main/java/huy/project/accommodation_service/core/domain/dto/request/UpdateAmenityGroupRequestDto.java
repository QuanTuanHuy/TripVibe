package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAmenityGroupRequestDto {
    private String name;
    private String type;
    private String description;
    private String icon;
    private Integer displayOrder;
    private Boolean isPopular;
}
