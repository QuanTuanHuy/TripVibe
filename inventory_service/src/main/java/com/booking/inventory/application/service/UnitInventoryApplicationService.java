package com.booking.inventory.application.service;

import com.booking.inventory.application.dto.UnitAvailabilityRequest;
import com.booking.inventory.application.dto.UnitInventoryDto;
import com.booking.inventory.application.dto.UnitInventorySyncRequest;
import com.booking.inventory.domain.model.Room;
import com.booking.inventory.domain.model.UnitInventory;
import com.booking.inventory.domain.service.UnitInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnitInventoryApplicationService {

    private final UnitInventoryService unitInventoryService;

    @Transactional
    public UnitInventoryDto syncUnitInventory(UnitInventorySyncRequest request) {
        log.info("Processing unit inventory sync: {}", request);
        UnitInventory unitInventory = unitInventoryService.syncUnitInventory(
                request.getAccommodationId(),
                request.getUnitId(),
                request.getUnitName(),
                request.getQuantity(),
                request.getRoomTypeId()
        );
        
        return mapToDto(unitInventory, 0);
    }

    @Transactional(readOnly = true)
    public List<UnitInventoryDto> findAllByAccommodationId(Long accommodationId) {
        return unitInventoryService.findAllByAccommodationId(accommodationId).stream()
                .map(unit -> mapToDto(unit, 0))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UnitInventoryDto> findByUnitId(Long unitId) {
        return unitInventoryService.findByUnitId(unitId)
                .map(unit -> mapToDto(unit, 0));
    }

    @Transactional(readOnly = true)
    public UnitInventoryDto checkAvailability(UnitAvailabilityRequest request) {
        Optional<UnitInventory> unitOpt = unitInventoryService.findByUnitId(request.getUnitId());
        
        if (unitOpt.isEmpty()) {
            return null;
        }
        
        UnitInventory unit = unitOpt.get();
        int availableRooms = unitInventoryService.countAvailableRoomsByUnitId(
                request.getUnitId(),
                request.getStartDate(),
                request.getEndDate()
        );
        
        return mapToDto(unit, availableRooms);
    }

    private UnitInventoryDto mapToDto(UnitInventory unitInventory, int availableRoomsCount) {
        // If availableRoomsCount is 0, count all rooms
        int availableRooms = availableRoomsCount > 0 ? 
                availableRoomsCount : unitInventory.getRooms().size();
        
        List<String> roomNumbers = unitInventory.getRooms().stream()
                .map(Room::getRoomNumber)
                .collect(Collectors.toList());
                
        return UnitInventoryDto.builder()
                .id(unitInventory.getId())
                .accommodationId(unitInventory.getAccommodationId())
                .unitId(unitInventory.getUnitId())
                .unitName(unitInventory.getUnitName())
                .quantity(unitInventory.getQuantity())
                .availableRooms(availableRooms)
                .roomNumbers(roomNumbers)
                .build();
    }
}
