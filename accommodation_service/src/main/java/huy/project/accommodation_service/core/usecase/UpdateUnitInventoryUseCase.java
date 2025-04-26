package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.UpdateUnitInventoryRequest;
import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import huy.project.accommodation_service.core.domain.entity.UnitInventoryEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IUnitInventoryPort;
import huy.project.accommodation_service.core.validation.AccommodationValidation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UpdateUnitInventoryUseCase {
    IUnitInventoryPort unitInventoryPort;

    GetUnitUseCase getUnitUseCase;

    AccommodationValidation accValidation;

    @Transactional(rollbackFor = Exception.class)
    public List<UnitInventoryEntity> blockInventory(Long userId, Long unitId, LocalDate startDate, LocalDate endDate, String reason) {
        // validate owner
        var isOwnerOfUnit = accValidation.isOwnerOfUnit(userId, unitId);
        if (!isOwnerOfUnit.getFirst()) {
            log.error("User {} is not owner of unit {}", userId, unitId);
            throw new AppException(ErrorCode.FORBIDDEN_UPDATE_UNIT_INVENTORY);
        }

        UnitEntity unit = getUnitUseCase.getUnitById(unitId);

        List<UnitInventoryEntity> unitInventories = unitInventoryPort.getInventoriesByUnitIdAndDateRange(unitId, startDate, endDate);
        var unitInventoryMap = unitInventories.stream()
                .collect(Collectors.toMap(UnitInventoryEntity::getDate, unitInventory -> unitInventory));

        var dates = startDate.datesUntil(endDate.plusDays(1)).toList();
        for (LocalDate date : dates) {
            if (!unitInventoryMap.containsKey(date)) {
                var unitInventory = UnitInventoryEntity.newInventory(unit, date);
                unitInventory.setIsBlocked(true);
                unitInventory.setBlockReason(reason);
                unitInventories.add(unitInventory);
            } else {
                var unitInventory = unitInventoryMap.get(date);
                unitInventory.setIsBlocked(true);
                unitInventory.setBlockReason(reason);
            }
        }

        unitInventories = unitInventoryPort.saveAll(unitInventories);
        unitInventories.sort(Comparator.comparing(UnitInventoryEntity::getDate));

        return unitInventories;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<UnitInventoryEntity> updateInventory(Long userId, Long unitId, List<UpdateUnitInventoryRequest> updates, String source) {
        var isOwnerOfUnit = accValidation.isOwnerOfUnit(userId, unitId);
        if (!isOwnerOfUnit.getFirst()) {
            log.error("User {} is not owner of unit {}", userId, unitId);
            throw new AppException(ErrorCode.FORBIDDEN_UPDATE_UNIT_INVENTORY);
        }

        UnitEntity unit = getUnitUseCase.getUnitById(unitId);

        List<UnitInventoryEntity> updateInventories = new ArrayList<>();

        for (var update : updates) {
            UnitInventoryEntity unitInventory = unitInventoryPort.getInventoryByUnitIdAndDate(unitId, update.getDate());
            if (unitInventory == null) {
                unitInventory = UnitInventoryEntity.builder()
                        .unitId(unitId)
                        .date(update.getDate())
                        .totalRooms(Math.min(update.getTotalRooms(), unit.getQuantity()))
                        .availableRooms(Math.min(update.getTotalRooms(), unit.getQuantity()))
                        .isBlocked(false)
                        .build();
            }

            if (update.getTotalRooms() != null) {
                int bookedRooms = unitInventory.getTotalRooms() - unitInventory.getAvailableRooms();
                unitInventory.setTotalRooms(update.getTotalRooms());
                unitInventory.setAvailableRooms(Math.max(0, update.getTotalRooms() - bookedRooms));
            }

            if (update.getAvailableRooms() != null) {
                unitInventory.setAvailableRooms(Math.min(unitInventory.getTotalRooms(), update.getAvailableRooms()));
            }

            if (update.getIsBlocked() != null) {
                unitInventory.setIsBlocked(update.getIsBlocked());
                unitInventory.setBlockReason(update.getBlockReason());
            }

            unitInventory.setLastSyncSource(source);
            unitInventory.setLastSyncTimestamp(Instant.now().toEpochMilli());

            updateInventories.add(unitInventory);
        }

        return unitInventoryPort.saveAll(updateInventories);
    }
}
