package com.booking.inventory.domain.service;

import com.booking.inventory.domain.model.Property;
import com.booking.inventory.domain.model.Room;
import com.booking.inventory.domain.model.RoomType;
import com.booking.inventory.domain.model.UnitInventory;
import com.booking.inventory.domain.repository.PropertyRepository;
import com.booking.inventory.domain.repository.RoomRepository;
import com.booking.inventory.domain.repository.RoomTypeRepository;
import com.booking.inventory.domain.repository.UnitInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnitInventoryService {
    
    private final UnitInventoryRepository unitInventoryRepository;
    private final PropertyRepository propertyRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final RoomAvailabilityService roomAvailabilityService;
    
    /**
     * Đồng bộ thông tin từ Accommodation Service và tạo/cập nhật UnitInventory
     */
    @Transactional
    public UnitInventory syncUnitInventory(Long accommodationId, Long unitId, String unitName, Integer quantity, Long roomTypeId) {
        log.info("Synchronizing unit inventory: accommodationId={}, unitId={}, name={}, quantity={}", 
                accommodationId, unitId, unitName, quantity);
        
        // Tìm hoặc tạo UnitInventory
        Optional<UnitInventory> existingUnit = unitInventoryRepository.findByUnitId(unitId);
        
        UnitInventory unitInventory;
        Property property = getOrCreateProperty(accommodationId);
        RoomType roomType = getOrCreateRoomType(roomTypeId, unitName);
        
        if (existingUnit.isPresent()) {
            unitInventory = existingUnit.get();
            log.debug("Found existing unit inventory: {}", unitInventory.getId());
            
            // Cập nhật thông tin nếu cần
            unitInventory.setUnitName(unitName);
            
            // Xử lý thay đổi số lượng
            int currentQuantity = unitInventory.getQuantity();
            if (currentQuantity != quantity) {
                log.info("Quantity changed from {} to {}", currentQuantity, quantity);
                handleQuantityChange(unitInventory, property, roomType, currentQuantity, quantity);
                unitInventory.setQuantity(quantity);
            }
        } else {
            // Tạo mới UnitInventory
            unitInventory = UnitInventory.builder()
                    .accommodationId(accommodationId)
                    .unitId(unitId)
                    .unitName(unitName)
                    .quantity(quantity)
                    .rooms(new ArrayList<>())
                    .build();
            
            unitInventoryRepository.save(unitInventory);
            log.info("Created new unit inventory: {}", unitInventory.getId());
            
            // Tạo các Room tương ứng với quantity
            createRoomsForUnit(unitInventory, property, roomType, quantity);
        }
        
        return unitInventoryRepository.save(unitInventory);
    }
    
    /**
     * Xử lý khi số lượng phòng thay đổi
     */
    private void handleQuantityChange(UnitInventory unitInventory, Property property, RoomType roomType, int currentQuantity, int newQuantity) {
        if (newQuantity > currentQuantity) {
            // Thêm phòng mới
            int roomsToAdd = newQuantity - currentQuantity;
            createRoomsForUnit(unitInventory, property, roomType, roomsToAdd);
            log.info("Added {} new rooms to unit {}", roomsToAdd, unitInventory.getUnitId());
        } else if (newQuantity < currentQuantity) {
            // Xóa bớt phòng (ưu tiên phòng không có booking)
            int roomsToRemove = currentQuantity - newQuantity;
            removeRoomsFromUnit(unitInventory, roomsToRemove);
            log.info("Removed {} rooms from unit {}", roomsToRemove, unitInventory.getUnitId());
        }
    }
    
    /**
     * Tạo các phòng mới cho một Unit
     */
    private void createRoomsForUnit(UnitInventory unitInventory, Property property, RoomType roomType, int quantity) {
        List<Room> existingRooms = roomRepository.findByUnitInventoryId(unitInventory.getId());
        int startNumber = existingRooms.size() + 1;
        
        List<Room> newRooms = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            String roomNumber = generateRoomNumber(unitInventory, startNumber + i);
            Room room = Room.builder()
                    .unitInventory(unitInventory)
                    .property(property)
                    .roomType(roomType)
                    .roomNumber(roomNumber)
                    .name(unitInventory.getUnitName() + " - " + roomNumber)
                    .description("Room " + roomNumber + " of type " + unitInventory.getUnitName())
                    .build();
            newRooms.add(room);
        }
        
        List<Room> savedRooms = roomRepository.saveAll(newRooms);
        log.debug("Created {} new rooms", savedRooms.size());
        
        // Khởi tạo availability cho các phòng mới (mặc định 365 ngày)
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(365);
        
        for (Room room : savedRooms) {
            roomAvailabilityService.initializeRoomAvailability(room.getId(), today, endDate);
        }
    }
    
    /**
     * Xóa bớt phòng khi giảm quantity
     */
    private void removeRoomsFromUnit(UnitInventory unitInventory, int roomsToRemove) {
        List<Room> rooms = roomRepository.findByUnitInventoryId(unitInventory.getId());
        
        // Sắp xếp để ưu tiên xóa phòng không có booking
        // Trong thực tế, cần kiểm tra phòng nào không có booking trước
        
        int removed = 0;
        for (int i = rooms.size() - 1; i >= 0 && removed < roomsToRemove; i--) {
            Room room = rooms.get(i);
            
            // Trong thực tế, cần kiểm tra room này có booking không
            roomRepository.delete(room);
            removed++;
        }
    }
    
    /**
     * Tạo số phòng duy nhất
     */
    private String generateRoomNumber(UnitInventory unitInventory, int number) {
        // Format: U{unitId}-{number} - ví dụ: U12-1, U12-2,...
        return String.format("U%d-%d", unitInventory.getUnitId(), number);
    }
    
    /**
     * Lấy hoặc tạo Property từ accommodationId
     */
    private Property getOrCreateProperty(Long accommodationId) {
        return propertyRepository.findByExternalId(accommodationId)
                .orElseGet(() -> {
                    Property newProperty = Property.builder()
                            .name("Accommodation " + accommodationId)
                            .externalId(accommodationId)
                            .description("Automatically created from Accommodation Service")
                            .build();
                    return propertyRepository.save(newProperty);
                });
    }
    
    /**
     * Lấy hoặc tạo RoomType từ thông tin Unit
     */
    private RoomType getOrCreateRoomType(Long roomTypeId, String name) {
        return roomTypeRepository.findByExternalId(roomTypeId)
                .orElseGet(() -> {
                    RoomType newRoomType = RoomType.builder()
                            .name(name)
                            .externalId(roomTypeId)
                            .description("Room type for " + name)
                            .build();
                    return roomTypeRepository.save(newRoomType);
                });
    }
    
    /**
     * Lấy danh sách các phòng có sẵn theo UnitId và khoảng thời gian
     */
    public List<Room> getAvailableRoomsByUnitId(Long unitId, LocalDate startDate, LocalDate endDate) {
        Optional<UnitInventory> unitOpt = unitInventoryRepository.findByUnitId(unitId);
        if (unitOpt.isEmpty()) {
            return List.of();
        }
        
        UnitInventory unit = unitOpt.get();
        List<Room> rooms = roomRepository.findByUnitInventoryId(unit.getId());
        
        return rooms.stream()
                .filter(room -> roomAvailabilityService.isRoomAvailable(room.getId(), startDate, endDate))
                .toList();
    }
    
    /**
     * Đếm số lượng phòng có sẵn cho một Unit
     */
    public int countAvailableRoomsByUnitId(Long unitId, LocalDate startDate, LocalDate endDate) {
        return getAvailableRoomsByUnitId(unitId, startDate, endDate).size();
    }
    
    /**
     * Tìm và lấy ra một phòng có sẵn từ danh sách phòng thuộc một Unit
     */
    public Optional<Room> findOneAvailableRoomByUnitId(Long unitId, LocalDate startDate, LocalDate endDate) {
        List<Room> availableRooms = getAvailableRoomsByUnitId(unitId, startDate, endDate);
        return availableRooms.isEmpty() ? Optional.empty() : Optional.of(availableRooms.get(0));
    }
    
    /**
     * Kiểm tra một unit có phòng trống không
     */
    public boolean isUnitAvailable(Long unitId, LocalDate startDate, LocalDate endDate) {
        return countAvailableRoomsByUnitId(unitId, startDate, endDate) > 0;
    }
    
    /**
     * Lấy tất cả các UnitInventory theo accommodationId
     */
    public List<UnitInventory> findAllByAccommodationId(Long accommodationId) {
        return unitInventoryRepository.findByAccommodationId(accommodationId);
    }
    
    /**
     * Lấy UnitInventory theo unitId
     */
    public Optional<UnitInventory> findByUnitId(Long unitId) {
        return unitInventoryRepository.findByUnitId(unitId);
    }
}
