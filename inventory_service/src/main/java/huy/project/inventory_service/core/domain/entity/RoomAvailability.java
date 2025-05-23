package huy.project.inventory_service.core.domain.entity;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoomAvailability {
    private Long id;

    private Long roomId;

    private LocalDate date;

    private RoomStatus status;

    private BigDecimal price;

    private BigDecimal basePrice;

    private Long bookingId;

    private Long guestId;

    private String lockId;

    private LocalDateTime lockExpirationTime;

    private LocalDateTime lastModified;

    // For housekeeping scheduling
    private Boolean needsCleaning;
    private Boolean needsMaintenance;
    private String maintenanceNotes;


    // For occupancy tracking
    private Integer guestCount;

    public boolean isAvailable() {
        return status == RoomStatus.AVAILABLE;
    }

    public void lockForBooking(String lockId, LocalDateTime expirationTime) {
        this.status = RoomStatus.TEMPORARILY_LOCKED;
        this.lockId = lockId;
        this.lockExpirationTime = expirationTime;
        this.lastModified = LocalDateTime.now();
    }

    public void confirmBooking(Long bookingId, Long guestId) {
        this.status = RoomStatus.BOOKED;
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.lockId = null;
        this.lockExpirationTime = null;
        this.lastModified = LocalDateTime.now();
    }

    public void cancelBooking() {
        this.status = RoomStatus.AVAILABLE;
        this.bookingId = null;
        this.lockId = null;
        this.lockExpirationTime = null;
        this.lastModified = LocalDateTime.now();
    }

    public void releaseLock() {
        if (this.status == RoomStatus.TEMPORARILY_LOCKED) {
            this.status = RoomStatus.AVAILABLE;
            this.lockId = null;
            this.lockExpirationTime = null;
            this.lastModified = LocalDateTime.now();
        }
    }

    public void checkIn() {
        this.status = RoomStatus.CHECKED_IN;
        this.needsCleaning = false;
        this.lastModified = LocalDateTime.now();
    }

    public void checkOut() {
        this.status = RoomStatus.CHECKED_OUT;
        this.needsCleaning = true;
        this.lastModified = LocalDateTime.now();
    }

    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
        this.lastModified = LocalDateTime.now();
    }

    public void updatePrice(BigDecimal newPrice) {
        this.price = newPrice;
        this.lastModified = LocalDateTime.now();
    }

}
