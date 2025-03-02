package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UnitPriceCalendarEntity {
    private Long id;
    private Long unitId;
    private Date date;
    private Long price;
}
