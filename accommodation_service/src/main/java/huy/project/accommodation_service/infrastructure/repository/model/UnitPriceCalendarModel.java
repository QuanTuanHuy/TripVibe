package huy.project.accommodation_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "unit_price_calendars")
public class UnitPriceCalendarModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_id")
    private Long unitId;

    @Column(name = "date")
    private Date date;

    @Column(name = "price")
    private Long price;
}
