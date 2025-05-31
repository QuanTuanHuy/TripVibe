package huy.project.rating_service.core.domain.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PendingReviewDto {
    private Long id;
    private Long userId;
    private Long accommodationId;
    private String accommodationName;
    private Long unitId;
    private String unitName;
    private Long bookingId;
    private Long startDate;
    private Long endDate;
    private String accommodationImageUrl;
    private String location;
}
