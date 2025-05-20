package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import huy.project.inventory_service.core.domain.entity.Room;
import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import huy.project.inventory_service.core.port.IRoomAvailabilityPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateRoomAvailabilityUseCase {
    private final IRoomAvailabilityPort roomAvailabilityPort;

    @Transactional(rollbackFor = Exception.class)
    public void createRoomAvailability(Room room, LocalDate startDate, LocalDate endDate) {

        List<RoomAvailability> existingAvailabilities = roomAvailabilityPort
                .getAvailabilitiesByRoomIdAndDateRange(room.getId(), startDate, endDate);
        Set<LocalDate> existingDates = existingAvailabilities.stream()
                .map(RoomAvailability::getDate)
                .collect(Collectors.toSet());

        List<LocalDate> datesToCreate = new ArrayList<>(startDate.datesUntil(endDate).toList());
        if (!CollectionUtils.isEmpty(existingDates)) {
            datesToCreate.removeAll(existingDates);
        }

        List<RoomAvailability> roomAvailabilities = datesToCreate.stream()
                .map(date -> RoomAvailability.builder()
                        .roomId(room.getId())
                        .date(date)
                        .status(RoomStatus.AVAILABLE)
                        .basePrice(room.getBasePrice())
                        .build())
                .toList();

        if (!CollectionUtils.isEmpty(roomAvailabilities)) {
            roomAvailabilityPort.saveAll(roomAvailabilities);
            log.info("Created {} room availabilities for room ID {} from {} to {}",
                    roomAvailabilities.size(), room.getId(), startDate, endDate);
        } else {
            log.info("No new room availabilities to create for room ID {} from {} to {}",
                    room.getId(), startDate, endDate);
        }
    }
}
