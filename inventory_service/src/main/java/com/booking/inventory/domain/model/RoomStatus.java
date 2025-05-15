package com.booking.inventory.domain.model;

/**
 * Enum representing the possible statuses of a room
 */
public enum RoomStatus {
    AVAILABLE,        // Room is available for booking
    TEMPORARILY_LOCKED, // Room is temporarily locked during booking process
    BOOKED,          // Room is booked
    OCCUPIED,        // Room is currently occupied by a guest
    MAINTENANCE,     // Room is under maintenance
    CLEANING         // Room is being cleaned
}
