package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import huy.project.inventory_service.core.domain.dto.request.AccommodationLockRequest;
import huy.project.inventory_service.core.domain.dto.request.UnitLockRequest;
import huy.project.inventory_service.core.domain.dto.response.AccommodationLockResponse;
import huy.project.inventory_service.core.domain.entity.Room;
import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.domain.exception.InsufficientAvailabilityException;
import huy.project.inventory_service.core.domain.exception.InvalidRequestException;
import huy.project.inventory_service.core.port.ILockPort;
import huy.project.inventory_service.core.port.IRoomAvailabilityPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LockRoomsUseCase {
    private final GetUnitUseCase getUnitUseCase;
    private final IRoomAvailabilityPort roomAvailabilityPort;
    private final ILockPort lockPort;

    private static final long DEFAULT_LOCK_TIME_SECONDS = 60 * 30;
    private static final long DEFAULT_TIME_TO_TRY_SECONDS = 5;

    @Transactional
    public AccommodationLockResponse execute(AccommodationLockRequest request) {
        validateLockRequest(request);

        String lockId = generateLockId(request);
        List<String> errors = new ArrayList<>();
        boolean success = false;

        long expiresAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(DEFAULT_LOCK_TIME_SECONDS);

        String distributedLockKey = "inventory_lock:" + request.getAccommodationId();
        boolean distributedLockAcquired = false;

        try {
            distributedLockAcquired = lockPort.acquireLock(distributedLockKey, DEFAULT_TIME_TO_TRY_SECONDS, 30, TimeUnit.SECONDS);

            if (!distributedLockAcquired) {
                return AccommodationLockResponse.builder()
                        .lockId(null)
                        .success(false)
                        .errors(Collections.singletonList("Could not acquire lock. Please try again."))
                        .build();
            }

            try {
                for (UnitLockRequest unitRequest : request.getUnitLockRequests()) {
                    lockUnitRooms(unitRequest, lockId);
                }

                boolean lockCreated = lockPort.acquireLock(lockId, DEFAULT_TIME_TO_TRY_SECONDS, DEFAULT_LOCK_TIME_SECONDS, TimeUnit.SECONDS);

                if (!lockCreated) {
                    throw new RuntimeException("Failed to create lock record in Redis");
                }

                success = true;
            } catch (Exception e) {
                releaseLockForRooms(lockId);
                errors.add(e.getMessage());
            }
        } finally {
            if (distributedLockAcquired) {
                lockPort.releaseLock(distributedLockKey);
            }
        }

        return AccommodationLockResponse.builder()
                .lockId(success ? lockId : null)
                .success(success)
                .errors(errors)
                .expiresAt(expiresAt)
                .build();
    }

    private void validateLockRequest(AccommodationLockRequest request) {
        if (request.getAccommodationId() == null) {
            throw new InvalidRequestException("Accommodation ID is required");
        }

        if (request.getUnitLockRequests() == null || request.getUnitLockRequests().isEmpty()) {
            throw new InvalidRequestException("At least one unit request is required");
        }

        for (UnitLockRequest unitRequest : request.getUnitLockRequests()) {
            if (unitRequest.getUnitId() == null) {
                throw new InvalidRequestException("Unit ID is required");
            }

            if (unitRequest.getQuantity() == null || unitRequest.getQuantity() <= 0) {
                throw new InvalidRequestException("Quantity must be a positive number");
            }

            if (unitRequest.getStartDate() == null) {
                throw new InvalidRequestException("Start date is required");
            }

            if (unitRequest.getEndDate() == null) {
                throw new InvalidRequestException("End date is required");
            }

            if (unitRequest.getStartDate().isAfter(unitRequest.getEndDate())) {
                throw new InvalidRequestException("Start date must be before end date");
            }

            if (unitRequest.getStartDate().isBefore(LocalDate.now())) {
                throw new InvalidRequestException("Start date cannot be in the past");
            }
        }
    }

    private String generateLockId(AccommodationLockRequest request) {
        return String.format("%d_%d_%d",
                request.getAccommodationId(),
                request.getUserId(),
                System.currentTimeMillis());
    }

    private void lockUnitRooms(UnitLockRequest unitRequest, String lockId) {
        // Lấy unit từ database
        Unit unit = getUnitUseCase.getUnitById(unitRequest.getUnitId());
        if (unit == null) {
            throw new InvalidRequestException("Unit not found: " + unitRequest.getUnitId());
        }

        List<Room> rooms = unit.getRooms();

        if (rooms == null || rooms.isEmpty()) {
            throw new InsufficientAvailabilityException("No rooms available for unit: " + unitRequest.getUnitId());
        }

        if (rooms.size() < unitRequest.getQuantity()) {
            throw new InsufficientAvailabilityException(
                    String.format("Requested %d rooms but only %d available for unit: %d",
                            unitRequest.getQuantity(), rooms.size(), unitRequest.getUnitId()));
        }

        Map<Long, List<RoomAvailability>> roomAvailabilityMap = new HashMap<>();
        for (Room room : rooms) {
            List<RoomAvailability> availabilities = roomAvailabilityPort.getAvailabilitiesByRoomIdAndDateRange(
                    room.getId(), unitRequest.getStartDate(), unitRequest.getEndDate());
            roomAvailabilityMap.put(room.getId(), availabilities);
        }

        List<Room> availableRooms = findAvailableRooms(rooms, roomAvailabilityMap, unitRequest);

        if (availableRooms.size() < unitRequest.getQuantity()) {
            throw new InsufficientAvailabilityException(
                    String.format("Not enough rooms available for the requested dates. Requested: %d, Available: %d",
                            unitRequest.getQuantity(), availableRooms.size()));
        }

        List<Room> roomsToLock = availableRooms.subList(0, unitRequest.getQuantity());

        List<RoomAvailability> updatedAvailabilities = new ArrayList<>();
        for (Room room : roomsToLock) {
            LocalDate currentDate = unitRequest.getStartDate();
            while (!currentDate.isAfter(unitRequest.getEndDate())) {
                LocalDate finalCurrentDate = currentDate;
                RoomAvailability availability = roomAvailabilityMap.get(room.getId())
                        .stream()
                        .filter(a -> a.getDate().equals(finalCurrentDate))
                        .findFirst()
                        .orElse(null);

                if (availability == null) {
                        // Tạo mới nếu chưa có
                        availability = RoomAvailability.builder()
                                .roomId(room.getId())
                                .date(currentDate)
                                .price(room.getBasePrice())
                                .basePrice(room.getBasePrice())
                                .build();
                }

                availability.lockForBooking(lockId, LocalDateTime.now().plusSeconds(DEFAULT_LOCK_TIME_SECONDS));

                updatedAvailabilities.add(availability);
                currentDate = currentDate.plusDays(1);
            }
        }

        roomAvailabilityPort.saveAll(updatedAvailabilities);
    }

    private List<Room> findAvailableRooms(List<Room> rooms,
                                          Map<Long, List<RoomAvailability>> roomAvailabilityMap,
                                          UnitLockRequest unitRequest) {
        return rooms.stream()
                .filter(room -> isRoomAvailable(room, roomAvailabilityMap.get(room.getId()),
                        unitRequest.getStartDate(), unitRequest.getEndDate()))
                .toList();
    }

    private boolean isRoomAvailable(Room room, List<RoomAvailability> availabilities,
                                    LocalDate startDate, LocalDate endDate) {
        if (availabilities == null || availabilities.isEmpty()) {
            return true;
        }

        Map<LocalDate, RoomAvailability> availabilityByDate = availabilities.stream()
                .collect(Collectors.toMap(RoomAvailability::getDate, Function.identity()));

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            RoomAvailability availability = availabilityByDate.get(currentDate);

            if (availability != null && !availability.isAvailable()) {
                return false;
            }

            currentDate = currentDate.plusDays(1);
        }

        return true;
    }

    private void releaseLockForRooms(String lockId) {
        int updatedCount = roomAvailabilityPort.updateStatusByLockId(lockId, RoomStatus.AVAILABLE);
        log.info("Released locks for lockId: {}. Updated {} records.", lockId, updatedCount);
    }
}
