package huy.project.rating_service.infrastructure.repository.model;

import huy.project.rating_service.core.domain.constant.RatingCriteriaType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "rating_details")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingDetailModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "rating_id", nullable = false)
    Long ratingId;

    @Column(name = "criteria_type", nullable = false)
    @Enumerated(EnumType.STRING)
    RatingCriteriaType criteriaType;

    @Column(name = "value", nullable = false)
    Double value;
}
