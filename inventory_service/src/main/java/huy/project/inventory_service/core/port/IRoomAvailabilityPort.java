package huy.project.inventory_service.core.port;

import huy.project.inventory_service.core.domain.entity.RoomAvailability;

import java.time.LocalDate;
import java.util.List;

public interface IRoomAvailabilityPort {
    List<RoomAvailability> saveAll(List<RoomAvailability> availabilities);

    List<RoomAvailability> getAvailabilitiesByRoomIdAndDateRange(Long roomId, LocalDate startDate, LocalDate endDate);

    RoomAvailability getAvailabilityByRoomIdAndDate(Long roomId, LocalDate date);
}
