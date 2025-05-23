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

    private static final long DEFAULT_LOCK_TIME_SECONDS = 60 * 30; // 30 phút
    private static final long DEFAULT_TIME_TO_TRY_SECONDS = 5; // 5 giây

    @Transactional
    public AccommodationLockResponse execute(AccommodationLockRequest request) {
        validateLockRequest(request);

        String lockId = generateLockId(request);
        List<String> errors = new ArrayList<>();
        boolean success = false;

        // Thiết lập thời gian hết hạn cho lock
        long expiresAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(DEFAULT_LOCK_TIME_SECONDS);

        // Bước 1: lấy distributed lock từ Redis để đảm bảo tính độc quyền
        // Sử dụng tên lock riêng cho mỗi accommodation để cho phép các accommodation khác nhau có thể được lock đồng thời
        String distributedLockKey = "inventory_lock:" + request.getAccommodationId();
        boolean distributedLockAcquired = false;

        try {
            // Thử lấy distributed lock với timeout ngắn (5 giây)
            distributedLockAcquired = lockPort.acquireLock(distributedLockKey, DEFAULT_TIME_TO_TRY_SECONDS, 30, TimeUnit.SECONDS);

            if (!distributedLockAcquired) {
                // Không thể lấy được distributed lock, có thể có người khác đang cố lock cùng accommodation
                return AccommodationLockResponse.builder()
                        .lockId(null)
                        .success(false)
                        .errors(Collections.singletonList("Could not acquire lock. Please try again."))
                        .build();
            }

            // Bước 2: Khi đã có distributed lock, bắt đầu khóa các phòng trong database
            try {
                // Lock cho từng unit request
                for (UnitLockRequest unitRequest : request.getUnitLockRequests()) {
                    lockUnitRooms(unitRequest, lockId);
                }

                // Nếu tất cả lệnh lock phòng thành công, tạo lock chính thức trong Redis với thời gian tồn tại lâu hơn
                boolean lockCreated = lockPort.acquireLock(lockId, DEFAULT_TIME_TO_TRY_SECONDS, DEFAULT_LOCK_TIME_SECONDS, TimeUnit.SECONDS);

                if (!lockCreated) {
                    throw new RuntimeException("Failed to create lock record in Redis");
                }

                success = true;
            } catch (Exception e) {
                // Nếu gặp lỗi, giải phóng tất cả các lock đã tạo
                releaseLockForRooms(lockId);
                errors.add(e.getMessage());
            }
        } finally {
            // Giải phóng distributed lock sau khi hoàn thành thao tác, bất kể thành công hay thất bại
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

        // Kiểm tra số lượng phòng có đủ không
        if (rooms.size() < unitRequest.getQuantity()) {
            throw new InsufficientAvailabilityException(
                    String.format("Requested %d rooms but only %d available for unit: %d",
                            unitRequest.getQuantity(), rooms.size(), unitRequest.getUnitId()));
        }

        // Kiểm tra tình trạng phòng trong khoảng thời gian yêu cầu
        Map<Long, List<RoomAvailability>> roomAvailabilityMap = new HashMap<>();
        for (Room room : rooms) {
            List<RoomAvailability> availabilities = roomAvailabilityPort.getAvailabilitiesByRoomIdAndDateRange(
                    room.getId(), unitRequest.getStartDate(), unitRequest.getEndDate());
            roomAvailabilityMap.put(room.getId(), availabilities);
        }

        // Tìm các phòng có thể lock
        List<Room> availableRooms = findAvailableRooms(rooms, roomAvailabilityMap, unitRequest);

        if (availableRooms.size() < unitRequest.getQuantity()) {
            throw new InsufficientAvailabilityException(
                    String.format("Not enough rooms available for the requested dates. Requested: %d, Available: %d",
                            unitRequest.getQuantity(), availableRooms.size()));
        }

        // Chỉ lấy số lượng phòng cần thiết
        List<Room> roomsToLock = availableRooms.subList(0, unitRequest.getQuantity());

        // Cập nhật trạng thái phòng thành TEMPORARILY_LOCKED
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

        // Lưu tất cả cập nhật vào database
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
        // Nếu không có bản ghi availability, phòng được coi là available
        if (availabilities == null || availabilities.isEmpty()) {
            return true;
        }

        // Kiểm tra tất cả các ngày trong khoảng thời gian
        Map<LocalDate, RoomAvailability> availabilityByDate = availabilities.stream()
                .collect(Collectors.toMap(RoomAvailability::getDate, Function.identity()));

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            RoomAvailability availability = availabilityByDate.get(currentDate);

            // Nếu ngày không có bản ghi availability hoặc trạng thái là AVAILABLE, phòng được coi là available
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
