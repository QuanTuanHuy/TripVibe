package huy.project.accommodation_service.core.domain.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnitInventoryEntity {
    Long id;

    Long unitId;
    LocalDate date;
    Integer totalRooms;
    Integer availableRooms;
    Boolean isBlocked;
    String blockReason;

    String lastSyncSource;
    Long lastSyncTimestamp;

    public static UnitInventoryEntity newInventory(UnitEntity unit, LocalDate date) {
        return UnitInventoryEntity.builder()
                .unitId(unit.getId())
                .date(date)
                .totalRooms(unit.getQuantity())
                .availableRooms(unit.getQuantity())
                .isBlocked(false)
                .blockReason(null)
                .lastSyncSource("MANUAL")
                .lastSyncTimestamp(System.currentTimeMillis())
                .build();
    }
}
