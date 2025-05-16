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
    private final GetRoomUseCase getRoomUseCase;

    @Transactional(rollbackFor = Exception.class)
    public void createRoomAvailability(Long roomId, LocalDate startDate, LocalDate endDate) {
        Room room = getRoomUseCase.getRoomById(roomId);

        List<RoomAvailability> existingAvailabilities = roomAvailabilityPort
                .getAvailabilitiesByRoomIdAndDateRange(roomId, startDate, endDate);
        Set<LocalDate> existingDates = existingAvailabilities.stream()
                .map(RoomAvailability::getDate)
                .collect(Collectors.toSet());

        List<LocalDate> datesToCreate = new ArrayList<>(startDate.datesUntil(endDate).toList());
        if (!CollectionUtils.isEmpty(existingDates)) {
            datesToCreate.removeAll(existingDates);
        }

        List<RoomAvailability> roomAvailabilities = datesToCreate.stream()
                .map(date -> RoomAvailability.builder()
                        .roomId(roomId)
                        .date(date)
                        .status(RoomStatus.AVAILABLE)
                        .basePrice(room.getBasePrice())
                        .build())
                .toList();

        if (!CollectionUtils.isEmpty(roomAvailabilities)) {
            roomAvailabilityPort.saveAll(roomAvailabilities);
            log.info("Created {} room availabilities for room ID {} from {} to {}",
                     roomAvailabilities.size(), roomId, startDate, endDate);
        } else {
            log.info("No new room availabilities to create for room ID {} from {} to {}",
                     roomId, startDate, endDate);
        }
    }
}
