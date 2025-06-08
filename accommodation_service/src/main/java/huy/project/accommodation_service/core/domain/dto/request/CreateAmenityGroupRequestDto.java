package huy.project.accommodation_service.core.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAmenityGroupRequestDto {
    private String name;
    private String type;
    private String description;
    private String icon;
    private Integer displayOrder;
    private Boolean isPopular;
}
