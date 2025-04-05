package huy.project.profile_service.core.domain.dto.request;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseParams {
    private Integer page;
    private Integer pageSize;
    private String sortBy;
    private String sortType;
}
