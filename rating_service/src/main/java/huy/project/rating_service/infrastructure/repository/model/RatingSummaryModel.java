package huy.project.rating_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "rating_summaries")
public class RatingSummaryModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "accommodation_id")
    private Long accommodationId;

    @Column(name = "number_of_ratings")
    private Integer numberOfRatings;

    @Column(name = "total_rating")
    private Long totalRating;

    @Column(name = "is_synced_with_search_service")
    private Boolean isSyncedWithSearchService;

    @Column(name = "distribution", columnDefinition = "TEXT")
    private String distribution;

    @Column(name = "criteria_averages", columnDefinition = "TEXT")
    private String criteriaAverages;
}
