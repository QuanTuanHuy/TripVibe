package com.booking.inventory.domain.repository;

import com.booking.inventory.domain.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    List<Room> findByPropertyId(Long propertyId);
    
    List<Room> findByRoomTypeId(Long roomTypeId);
    
    Optional<Room> findByRoomNumber(String roomNumber);
    
    List<Room> findByUnitInventoryId(Long unitInventoryId);
    
    List<Room> findByUnitInventoryUnitId(Long unitId);
}
