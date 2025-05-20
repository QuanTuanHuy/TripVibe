package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.entity.Room;
import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.port.IRoomPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateRoomUseCase {
    private final IRoomPort roomPort;

    private final GetRoomUseCase getRoomUseCase;
    private final CreateRoomAvailabilityUseCase createRoomAvailabilityUseCase;

    @Transactional(rollbackFor = Exception.class)
    public void createRoomsForUnit(Unit unit, int quantity) {
        if (unit.getId() == null) {
            throw new IllegalArgumentException("Unit ID cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        List<Room> existingRooms = getRoomUseCase.getRoomsByUnitId(unit.getId());
        int startNumber = existingRooms.size() + 1;

        List<Room> newRooms = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            Room room = Room.builder()
                    .unitId(unit.getId())
                    .roomNumber(generateRoomNumber(unit, startNumber + i))
                    .name(generateRoomName(unit, startNumber + i))
                    .basePrice(unit.getBasePrice())
                    .description(null)
                    .build();
            newRooms.add(room);
        }
        List<Room> savedRooms = roomPort.saveAll(newRooms);
        log.info("Saved {} rooms for unit ID {} in accommodation ID {}",
                savedRooms.size(), unit.getId(), unit.getAccommodationId());

        // initial availability for new room (default is 365 days)
        LocalDate now = LocalDate.now();
        LocalDate endDate = now.plusDays(365);
        savedRooms.stream()
                .parallel()
                .forEach(room ->
                        createRoomAvailabilityUseCase.createRoomAvailability(room, now, endDate));
    }

    private String generateRoomNumber(Unit unit, int number) {
        // Format: U{unitId}-{number} - ví dụ: U12-1, U12-2,...
        return String.format("U%d-%d", unit.getId(), number);
    }

    private String generateRoomName(Unit unit, int number) {
        // Format: {unitName} {number} - ví dụ: Deluxe 1, Deluxe 2,...
        return String.format("%s %d", unit.getUnitName(), number);
    }
}
