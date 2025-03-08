package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UnitPriceCalendarEntity {
    private Long id;
    private Long unitId;
    private LocalDate date;
    private Long price;
}
