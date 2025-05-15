package com.booking.inventory.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_availabilities", 
       indexes = {
           @Index(name = "idx_room_date", columnList = "room_id, date"),
           @Index(name = "idx_booking_id", columnList = "booking_id"),
           @Index(name = "idx_status_lock_expiration", columnList = "status, lock_expiration_date"),
           @Index(name = "idx_date_status", columnList = "date, status")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAvailability {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;
    
    private LocalDate date;
    
    @Enumerated(EnumType.STRING)
    private RoomStatus status;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    
    // Base price before discounts or dynamic pricing
    @Column(precision = 10, scale = 2)
    private BigDecimal basePrice;
    
    // The booking this availability is assigned to
    private String bookingId;
    
    // For temporary locks during booking process
    private String lockId;
    private LocalDate lockExpirationDate;
    
    // Track modifications for audit and performance optimization
    private LocalDateTime lastModified;
    
    // For housekeeping scheduling
    private Boolean needsCleaning;
    private Boolean needsMaintenance;
    private String maintenanceNotes;
    
    // For occupancy tracking
    private Integer guestCount;
    
    // Version for optimistic locking
    @Version
    private Integer version;
      // Method to check if the room is available on this date
    public boolean isAvailable() {
        return status == RoomStatus.AVAILABLE;
    }
    
    // Method to lock the room for a specific booking
    public void lockForBooking(String lockId, LocalDate expirationDate) {
        this.status = RoomStatus.TEMPORARILY_LOCKED;
        this.lockId = lockId;
        this.lockExpirationDate = expirationDate;
        this.lastModified = LocalDateTime.now();
    }
    
    // Method to confirm booking
    public void confirmBooking(String bookingId) {
        this.status = RoomStatus.BOOKED;
        this.bookingId = bookingId;
        this.lockId = null;
        this.lockExpirationDate = null;
        this.lastModified = LocalDateTime.now();
    }
    
    // Method to release lock
    public void releaseLock() {
        if (this.status == RoomStatus.TEMPORARILY_LOCKED) {
            this.status = RoomStatus.AVAILABLE;
            this.lockId = null;
            this.lockExpirationDate = null;
            this.lastModified = LocalDateTime.now();
        }
    }
    
    // Method to mark as occupied (check-in)
    public void checkIn() {
        this.status = RoomStatus.OCCUPIED;
        this.needsCleaning = false; // Reset cleaning flag
        this.lastModified = LocalDateTime.now();
    }
    
    // Method to mark as needing cleaning after check-out
    public void checkOut() {
        this.status = RoomStatus.CLEANING;
        this.needsCleaning = true;
        this.lastModified = LocalDateTime.now();
    }
    
    // Method to mark as cleaned and available again
    public void markCleaned() {
        this.status = RoomStatus.AVAILABLE;
        this.needsCleaning = false;
        this.lastModified = LocalDateTime.now();
    }
    
    // Method to mark for maintenance
    public void markForMaintenance() {
        this.status = RoomStatus.MAINTENANCE;
        this.needsMaintenance = true;
        this.lastModified = LocalDateTime.now();
    }
    
    // Method to complete maintenance
    public void completeMaintenance() {
        this.status = RoomStatus.CLEANING; // Requires cleaning after maintenance
        this.needsMaintenance = false;
        this.needsCleaning = true;
        this.maintenanceNotes = null;
        this.lastModified = LocalDateTime.now();
    }
    
    // Method to set guest count for occupancy tracking
    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
        this.lastModified = LocalDateTime.now();
    }
    
    // Method to update price (for dynamic pricing)
    public void updatePrice(BigDecimal newPrice) {
        this.price = newPrice;
        this.lastModified = LocalDateTime.now();
    }
    
    // Method to schedule maintenance
    public void scheduleMaintenance(String notes) {
        this.needsMaintenance = true;
        this.maintenanceNotes = notes;
        this.lastModified = LocalDateTime.now();
    }
}
