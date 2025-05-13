package huy.project.rating_service.core.domain.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class BaseParams {
    @Min(value = 0, message = "Trang phải lớn hơn hoặc bằng 0")
    private Integer page;

    @Min(value = 1, message = "Kích thước trang phải lớn hơn 0")
    private Integer pageSize;

    private String sortBy;
    private String sortType;
}