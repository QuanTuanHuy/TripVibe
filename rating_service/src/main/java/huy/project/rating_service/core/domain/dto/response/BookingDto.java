package huy.project.rating_service.core.domain.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BookingDto {
    private Long bookingId;
    private Long userId;
    private Long startDate;
    private Long endDate;
    private Integer numberOfAdult;
    private Integer numberOfChild;
}
