package com.booking.inventory.domain.repository;

import com.booking.inventory.domain.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    
    Optional<Property> findByName(String name);
    
    List<Property> findByCity(String city);
    
    List<Property> findByCountry(String country);
    
    List<Property> findByCityAndCountry(String city, String country);
    
    Optional<Property> findByExternalId(Long externalId);
}
