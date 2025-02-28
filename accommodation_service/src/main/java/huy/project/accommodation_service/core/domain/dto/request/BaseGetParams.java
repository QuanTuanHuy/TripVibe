package huy.project.accommodation_service.core.domain.dto.request;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class BaseGetParams {
    private Integer page;
    private Integer pageSize;
    private String sortBy;
    private String sortType;
}
