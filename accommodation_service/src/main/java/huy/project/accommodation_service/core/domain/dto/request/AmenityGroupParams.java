package huy.project.accommodation_service.core.domain.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
public class AmenityGroupParams extends BaseGetParams {
    String type;
    Boolean isPopular;
}
