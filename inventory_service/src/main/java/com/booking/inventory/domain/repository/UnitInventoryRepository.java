package com.booking.inventory.domain.repository;

import com.booking.inventory.domain.model.UnitInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitInventoryRepository extends JpaRepository<UnitInventory, Long> {
    
    List<UnitInventory> findByAccommodationId(Long accommodationId);
    
    Optional<UnitInventory> findByUnitId(Long unitId);
    
    List<UnitInventory> findByAccommodationIdIn(List<Long> accommodationIds);
}
