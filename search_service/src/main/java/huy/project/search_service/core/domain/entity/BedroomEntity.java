package huy.project.search_service.core.domain.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BedroomEntity {
    private Long id;
    private Integer quantity;
    private List<BedEntity> beds;
}
