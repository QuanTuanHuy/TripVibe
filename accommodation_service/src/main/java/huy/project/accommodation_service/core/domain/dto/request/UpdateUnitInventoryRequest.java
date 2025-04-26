package huy.project.accommodation_service.core.domain.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUnitInventoryRequest {
    LocalDate date;
    Integer totalRooms;
    Integer availableRooms;
    Boolean isBlocked;
    String blockReason;
}
