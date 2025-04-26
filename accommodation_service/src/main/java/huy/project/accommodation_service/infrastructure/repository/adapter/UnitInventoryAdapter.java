package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.entity.UnitInventoryEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IUnitInventoryPort;
import huy.project.accommodation_service.infrastructure.repository.IUnitInventoryRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.UnitInventoryMapper;
import huy.project.accommodation_service.infrastructure.repository.model.UnitInventoryModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UnitInventoryAdapter implements IUnitInventoryPort {
    IUnitInventoryRepository unitInventoryRepository;

    @Override
    public List<UnitInventoryEntity> saveAll(List<UnitInventoryEntity> unitInventories) {
        try {
            List<UnitInventoryModel> models = UnitInventoryMapper.INSTANCE.toListModel(unitInventories);
            return UnitInventoryMapper.INSTANCE.toListEntity(unitInventoryRepository.saveAll(models));
        } catch (Exception e) {
            throw new AppException(ErrorCode.SAVE_UNIT_INVENTORY_FAILED);
        }
    }

    @Override
    public List<UnitInventoryEntity> getInventoriesByUnitIdAndDateRange(Long unitId, LocalDate startDate, LocalDate endDate) {
        return UnitInventoryMapper.INSTANCE.toListEntity(unitInventoryRepository.findByUnitIdAndDateBetween(unitId, startDate, endDate));
    }

    @Override
    public UnitInventoryEntity getInventoryByUnitIdAndDate(Long unitId, LocalDate date) {
        return UnitInventoryMapper.INSTANCE.toEntity(
                unitInventoryRepository.findByUnitIdAndDate(unitId, date).orElse(null));
    }
}
