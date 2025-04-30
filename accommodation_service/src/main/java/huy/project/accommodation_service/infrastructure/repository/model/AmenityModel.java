package huy.project.accommodation_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "amenities")
public class AmenityModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "icon")
    private String icon;

    @Column(name = "description")
    private String description;

    @Column(name = "is_paid")
    private Boolean isPaid;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "available_time")
    private String availableTime; // e.g., "24/7", "8:00-22:00"

    @Column(name = "is_highlighted")
    private Boolean isHighlighted;

    @Column(name = "is_filterable")
    private Boolean isFilterable;

    @Column(name = "group_id")
    private Long groupId;
}
