package huy.project.accommodation_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "accommodations")
public class AccommodationModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "host_id")
    private Long hostId;

    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "type_id")
    private Long typeId;

    @Column(name = "currency_id")
    private Long currencyId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "check_in_time_from")
    private LocalTime checkInTimeFrom;

    @Column(name = "check_in_time_to")
    private LocalTime checkInTimeTo;

    @Column(name = "check_out_time_from")
    private LocalTime checkOutTimeFrom;

    @Column(name = "check_out_time_to")
    private LocalTime checkOutTimeTo;

    @Column(name = "is_verified")
    private Boolean isVerified;
}
