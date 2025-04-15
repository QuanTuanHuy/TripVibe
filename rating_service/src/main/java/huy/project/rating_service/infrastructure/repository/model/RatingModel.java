package huy.project.rating_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "ratings")
public class RatingModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "accommodation_id")
    private Long accommodationId;
    @Column(name = "unit_id")
    private Long unitId;
    @Column(name = "value")
    private Double value;
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    @Column(name = "language_id")
    private Long languageId;
    @Column(name = "number_of_helpful")
    private Integer numberOfHelpful;
    @Column(name = "number_of_un_helpful")
    private Integer numberOfUnHelpful;
}
