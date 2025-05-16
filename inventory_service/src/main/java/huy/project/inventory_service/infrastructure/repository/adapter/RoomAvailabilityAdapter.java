package huy.project.inventory_service.infrastructure.repository.adapter;

import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import huy.project.inventory_service.core.port.IRoomAvailabilityPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RoomAvailabilityAdapter implements IRoomAvailabilityPort {
    @Override
    public List<RoomAvailability> saveAll(List<RoomAvailability> availabilities) {
        return List.of();
    }

    @Override
    public List<RoomAvailability> getAvailabilitiesByRoomIdAndDateRange(Long roomId, LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public RoomAvailability getAvailabilityByRoomIdAndDate(Long roomId, LocalDate date) {
        return null;
    }
}
