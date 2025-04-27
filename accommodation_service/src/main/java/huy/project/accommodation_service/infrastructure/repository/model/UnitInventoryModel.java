package huy.project.accommodation_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "unit_inventories")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnitInventoryModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "unit_id", nullable = false)
    Long unitId;
    @Column(name = "date", nullable = false)
    LocalDate date;
    @Column(name = "total_rooms", nullable = false)
    Integer totalRooms;
    @Column(name = "available_rooms", nullable = false)
    Integer availableRooms;
    @Column(name = "is_blocked")
    Boolean isBlocked;
    @Column(name = "block_reason")
    String blockReason;

    @Column(name = "last_sync_source")
    String lastSyncSource;

    @Column(name = "last_sync_timestamp")
    Instant lastSyncTimestamp;
}
