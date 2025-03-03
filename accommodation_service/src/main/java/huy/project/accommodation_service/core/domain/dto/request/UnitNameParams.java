package huy.project.accommodation_service.core.domain.dto.request;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnitNameParams extends BaseGetParams {
    private String name;
}
