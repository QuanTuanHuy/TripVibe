package com.booking.inventory.domain.repository;

import com.booking.inventory.domain.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    
    List<RoomType> findByNameContainingIgnoreCase(String name);
    
    List<RoomType> findByMaxOccupancyGreaterThanEqual(Integer occupancy);
    
    List<RoomType> findByMaxAdultsGreaterThanEqualAndMaxChildrenGreaterThanEqual(Integer adults, Integer children);
    
    Optional<RoomType> findByExternalId(Long externalId);
}
