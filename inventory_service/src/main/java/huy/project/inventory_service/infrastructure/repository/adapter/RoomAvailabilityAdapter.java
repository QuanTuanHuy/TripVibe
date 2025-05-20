package huy.project.inventory_service.infrastructure.repository.adapter;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import huy.project.inventory_service.core.port.IRoomAvailabilityPort;
import huy.project.inventory_service.infrastructure.repository.IRoomAvailabilityRepository;
import huy.project.inventory_service.infrastructure.repository.mapper.RoomAvailabilityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomAvailabilityAdapter implements IRoomAvailabilityPort {

    private final IRoomAvailabilityRepository roomAvailabilityRepository;

    @Override
    public List<RoomAvailability> saveAll(List<RoomAvailability> availabilities) {
        var models = RoomAvailabilityMapper.INSTANCE.toModelList(availabilities);
        return RoomAvailabilityMapper.INSTANCE
                .toEntityList(roomAvailabilityRepository.saveAll(models));
    }

    @Override
    public List<RoomAvailability> getAvailabilitiesByRoomIdAndDateRange(Long roomId, LocalDate startDate, LocalDate endDate) {
        return RoomAvailabilityMapper.INSTANCE.toEntityList(
                roomAvailabilityRepository.findByRoomIdAndDateBetween(roomId, startDate, endDate));
    }

    @Override
    public RoomAvailability getAvailabilityByRoomIdAndDate(Long roomId, LocalDate date) {
        return roomAvailabilityRepository.findByRoomIdAndDate(roomId, date)
                .map(RoomAvailabilityMapper.INSTANCE::toEntity)
                .orElse(null);
    }

    @Override
    public int releaseLocksById(String lockId) {
        return roomAvailabilityRepository.releaseLocksById(lockId);
    }

    @Override
    public List<RoomAvailability> findByLockId(String lockId) {
        return RoomAvailabilityMapper.INSTANCE.toEntityList(
                roomAvailabilityRepository.findByLockId(lockId));
    }

    @Override
    public int updateStatusByLockId(String lockId, RoomStatus status) {
        return roomAvailabilityRepository.updateStatusByLockId(lockId, status);
    }

    @Override
    public int releaseExpiredLocks(LocalDateTime now) {
        return roomAvailabilityRepository.releaseExpiredLocks(now);
    }
}
