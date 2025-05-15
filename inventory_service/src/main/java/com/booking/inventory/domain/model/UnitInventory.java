package com.booking.inventory.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "unit_inventories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UnitInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long accommodationId;
    
    private Long unitId;
    
    private String unitName;
    
    private Integer quantity;
    
    // Các phòng thuộc Unit này
    @OneToMany(mappedBy = "unitInventory", cascade = CascadeType.ALL)
    private List<Room> rooms;
}
