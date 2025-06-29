package huy.project.accommodation_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "units")
public class UnitModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "accommodation_id")
    private Long accommodationId;

    @Column(name = "unit_name_id")
    private Long unitNameId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price_per_night")
    private Long pricePerNight;

    @Column(name = "max_adults")
    private Integer maxAdults;

    @Column(name = "max_children")
    private Integer maxChildren;

    @Column(name = "use_shared_bathroom")
    private Boolean useSharedBathroom;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "quantity")
    private Integer quantity;
}
