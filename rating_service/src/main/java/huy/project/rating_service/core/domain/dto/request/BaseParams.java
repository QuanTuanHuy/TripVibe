package huy.project.rating_service.core.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
