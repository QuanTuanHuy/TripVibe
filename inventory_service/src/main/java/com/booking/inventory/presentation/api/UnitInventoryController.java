package com.booking.inventory.presentation.api;

import com.booking.inventory.application.dto.UnitAvailabilityRequest;
import com.booking.inventory.application.dto.UnitInventoryDto;
import com.booking.inventory.application.dto.UnitInventorySyncRequest;
import com.booking.inventory.application.service.UnitInventoryApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/units")
@RequiredArgsConstructor
@Tag(name = "Unit Inventory API", description = "API for managing unit inventories from Accommodation Service")
@Slf4j
public class UnitInventoryController {

    private final UnitInventoryApplicationService unitInventoryService;
    
    @PostMapping
    @Operation(summary = "Synchronize unit inventory", 
              description = "Create or update a unit inventory based on data from Accommodation Service")
    public ResponseEntity<UnitInventoryDto> syncUnitInventory(@RequestBody UnitInventorySyncRequest request) {
        UnitInventoryDto result = unitInventoryService.syncUnitInventory(request);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/accommodation/{accommodationId}")
    @Operation(summary = "Get units by accommodation", 
              description = "Get all unit inventories for a specific accommodation")
    public ResponseEntity<List<UnitInventoryDto>> getByAccommodation(@PathVariable Long accommodationId) {
        List<UnitInventoryDto> units = unitInventoryService.findAllByAccommodationId(accommodationId);
        return ResponseEntity.ok(units);
    }
    
    @GetMapping("/unit/{unitId}")
    @Operation(summary = "Get unit by ID", 
              description = "Get unit inventory details by unit ID")
    public ResponseEntity<UnitInventoryDto> getByUnitId(@PathVariable Long unitId) {
        return unitInventoryService.findByUnitId(unitId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/availability")
    @Operation(summary = "Check unit availability", 
              description = "Check availability of rooms for a specific unit and date range")
    public ResponseEntity<UnitInventoryDto> checkAvailability(@RequestBody UnitAvailabilityRequest request) {
        UnitInventoryDto result = unitInventoryService.checkAvailability(request);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
