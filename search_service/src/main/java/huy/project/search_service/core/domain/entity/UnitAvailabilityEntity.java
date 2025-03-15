package huy.project.search_service.core.domain.entity;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitAvailabilityEntity {
    private Date date;
    private Integer availableCount;
}
