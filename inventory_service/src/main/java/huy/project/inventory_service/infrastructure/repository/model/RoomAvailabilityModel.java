package huy.project.inventory_service.infrastructure.repository.model;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_availabilities",
       uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "date"}),
       indexes = {
           @Index(name = "idx_room_availability_room_id", columnList = "room_id"),
           @Index(name = "idx_room_availability_date", columnList = "date"),
           @Index(name = "idx_room_availability_status", columnList = "status"),
           @Index(name = "idx_room_availability_lock_id", columnList = "lock_id")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAvailabilityModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id")
    private Long roomId;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private RoomStatus status;
    
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice;
    
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "guest_id")
    private Long guestId;
    
    @Column(name = "lock_id")
    private String lockId;
    
    @Column(name = "lock_expiration_time")
    private LocalDateTime lockExpirationTime;
    
    @Column(name = "last_modified")
    private LocalDateTime lastModified;
    
    // For housekeeping scheduling
    @Column(name = "needs_cleaning")
    private Boolean needsCleaning;
    
    @Column(name = "needs_maintenance")
    private Boolean needsMaintenance;
    
    @Column(name = "maintenance_notes", columnDefinition = "TEXT")
    private String maintenanceNotes;
    
    // For occupancy tracking
    @Column(name = "guest_count")
    private Integer guestCount;

}