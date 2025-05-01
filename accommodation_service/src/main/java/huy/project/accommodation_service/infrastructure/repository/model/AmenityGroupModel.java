package huy.project.accommodation_service.infrastructure.repository.model;

import huy.project.accommodation_service.core.domain.constant.AmenityGroupType;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "amenity_groups")
public class AmenityGroupModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "icon")
    private String icon;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_popular")
    private Boolean isPopular;
}
