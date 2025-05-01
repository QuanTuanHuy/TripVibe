package huy.project.accommodation_service.core.domain.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccommodationThumbnailParams {
    List<Long> ids;
    Integer guestCount;
    LocalDate startDate;
    LocalDate endDate;
}
