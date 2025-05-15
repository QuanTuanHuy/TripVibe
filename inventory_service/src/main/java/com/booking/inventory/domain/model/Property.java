package com.booking.inventory.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private String description;
    
    private String address;
    
    private String city;
    
    private String state;
    
    private String country;
    
    private String zipCode;
    
    private String latitude;
    
    private String longitude;
    
    // ID của accommodation trong Accommodation Service
    @Column(unique = true)
    private Long externalId;
    
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();
    
    // Add a room to this property
    public void addRoom(Room room) {
        rooms.add(room);
        room.setProperty(this);
    }
    
    // Remove a room from this property
    public void removeRoom(Room room) {
        rooms.remove(room);
        room.setProperty(null);
    }
}
