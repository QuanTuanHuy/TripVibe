package huy.project.rating_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PendingReviewEntity {
    private Long id;
    private Long userId;
    private Long accommodationId;
    private Long unitId;
    private Long bookingId;
}
