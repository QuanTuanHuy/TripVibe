package com.booking.inventory.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private String description;
    
    private Integer maxOccupancy;
    
    private Integer maxAdults;
    
    private Integer maxChildren;
      @Column(precision = 10, scale = 2)
    private BigDecimal basePrice;
    
    // ID tương ứng với unitNameId trong Accommodation Service
    @Column(unique = true)
    private Long externalId;
    
    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();
    
    // Add a room to this room type
    public void addRoom(Room room) {
        rooms.add(room);
        room.setRoomType(this);
    }
    
    // Remove a room from this room type
    public void removeRoom(Room room) {
        rooms.remove(room);
        room.setRoomType(null);
    }
}
