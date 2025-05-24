package huy.project.inventory_service.core.domain.constant;

public enum RoomStatus {
    AVAILABLE,        // Room is available for booking
    TEMPORARILY_LOCKED, // Room is temporarily locked during booking process
    BOOKED,          // Room is booked
    CHECKED_IN,       // Guest has checked in
    CHECKED_OUT,      // Guest has checked out
    OCCUPIED,        // A guest currently occupies room
    MAINTENANCE,     // Room is under maintenance
    CLEANING         // Room is being cleaned
}
