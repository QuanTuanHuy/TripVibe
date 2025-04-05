package huy.project.accommodation_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "unit_amenities")
public class UnitAmenityModel extends AuditTable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_id")
    private Long unitId;

    @Column(name = "amenity_id")
    private Long amenityId;

    @Column(name = "fee")
    private Long fee;

    @Column(name = "need_to_reserve")
    private Boolean needToReserve;
}
