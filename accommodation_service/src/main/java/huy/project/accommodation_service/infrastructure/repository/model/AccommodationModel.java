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

    @Column(name = "name", length = 1000)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "check_in_time_from")
    private Long checkInTimeFrom;

    @Column(name = "check_in_time_to")
    private Long checkInTimeTo;

    @Column(name = "check_out_time_from")
    private Long checkOutTimeFrom;

    @Column(name = "check_out_time_to")
    private Long checkOutTimeTo;

    @Column(name = "is_verified")
    private Boolean isVerified;
}
