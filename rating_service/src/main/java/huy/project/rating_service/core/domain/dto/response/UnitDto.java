package huy.project.rating_service.core.domain.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UnitDto {
    private Long id;
    private String name;
    private Long accommodationId;
    private Integer quantity;
}
