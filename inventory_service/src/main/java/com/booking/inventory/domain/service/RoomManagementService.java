package com.booking.inventory.domain.service;

import com.booking.inventory.domain.model.Property;
import com.booking.inventory.domain.model.Room;
import com.booking.inventory.domain.model.RoomStatus;
import com.booking.inventory.domain.model.RoomType;
import com.booking.inventory.domain.repository.PropertyRepository;
import com.booking.inventory.domain.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomManagementService {
    
    private final RoomRepository roomRepository;
    private final PropertyRepository propertyRepository;
    private final RoomAvailabilityService roomAvailabilityService;
    
    /**
     * Create a new room
     * @param propertyId property id
     * @param roomTypeId room type id
     * @param roomNumber room number
     * @param name room name
     * @param description room description
     * @return the created room
     */
    @Transactional
    public Room createRoom(Long propertyId, Long roomTypeId, String roomNumber, String name, String description) {
        // Get property
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + propertyId));
        
        // Check if room number already exists in the property
        Optional<Room> existingRoom = roomRepository.findByRoomNumber(roomNumber);
        if (existingRoom.isPresent() && existingRoom.get().getProperty().getId().equals(propertyId)) {
            throw new IllegalArgumentException("Room number already exists in this property: " + roomNumber);
        }
        
        // Create and save the room
        Room room = Room.builder()
                .roomNumber(roomNumber)
                .name(name)
                .description(description)
                .status(RoomStatus.AVAILABLE)
                .property(property)
                .build();
        
        return roomRepository.save(room);
    }
    
    /**
     * Get all rooms for a property
     * @param propertyId property id
     * @return list of rooms
     */
    public List<Room> getRoomsByProperty(Long propertyId) {
        return roomRepository.findByPropertyId(propertyId);
    }
    
    /**
     * Get all rooms for a room type
     * @param roomTypeId room type id
     * @return list of rooms
     */
    public List<Room> getRoomsByType(Long roomTypeId) {
        return roomRepository.findByRoomTypeId(roomTypeId);
    }
    
    /**
     * Update room status
     * @param roomId room id
     * @param status new status
     * @return updated room
     */
    @Transactional
    public Room updateRoomStatus(Long roomId, RoomStatus status) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));
        room.setStatus(status);
        return roomRepository.save(room);
    }
    
    /**
     * Initialize availability for a room for the next year
     * @param roomId room id
     */
    @Transactional
    public void initializeRoomAvailabilityForYear(Long roomId) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);
        roomAvailabilityService.initializeRoomAvailability(roomId, startDate, endDate);
    }
    
    /**
     * Set a room for maintenance
     * @param roomId room id
     * @param startDate start date
     * @param endDate end date
     * @param reason maintenance reason
     */
    @Transactional
    public void setRoomForMaintenance(Long roomId, LocalDate startDate, LocalDate endDate, String reason) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));
        
        // Update room status
        room.setStatus(RoomStatus.MAINTENANCE);
        roomRepository.save(room);
        
        // Update availability records
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            roomAvailabilityService.initializeRoomAvailability(roomId, currentDate, currentDate);
            
            // We need to update the availability record after initialization
            room.getAvailabilities().stream()
                    .filter(a -> a.getDate().equals(currentDate))
                    .forEach(a -> {
                        a.markForMaintenance();
                    });
            
            currentDate = currentDate.plusDays(1);
        }
        
        roomRepository.save(room);
    }
}
