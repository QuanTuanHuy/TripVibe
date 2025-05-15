package com.booking.inventory.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String roomNumber;
    
    private String name;
    
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_inventory_id")
    private UnitInventory unitInventory;
    
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomAvailability> availabilities = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    private RoomStatus status;
    
    // Add a room availability entry
    public void addAvailability(RoomAvailability availability) {
        availabilities.add(availability);
        availability.setRoom(this);
    }
    
    // Remove a room availability entry
    public void removeAvailability(RoomAvailability availability) {
        availabilities.remove(availability);
        availability.setRoom(null);
    }
}
