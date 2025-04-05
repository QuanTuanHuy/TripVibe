package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import huy.project.accommodation_service.core.port.IUnitPort;
import huy.project.accommodation_service.infrastructure.repository.IUnitRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.UnitMapper;
import huy.project.accommodation_service.infrastructure.repository.model.UnitModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitAdapter implements IUnitPort {
    private final IUnitRepository unitRepository;

    @Override
    public UnitEntity save(UnitEntity unit) {
        UnitModel unitModel = UnitMapper.INSTANCE.toModel(unit);
        return UnitMapper.INSTANCE.toEntity(unitRepository.save(unitModel));
    }

    @Override
    public List<UnitEntity> getUnitsByAccommodationId(Long accommodationId) {
        return UnitMapper.INSTANCE.toListEntity(unitRepository.findByAccommodationId(accommodationId));
    }

    @Override
    public UnitEntity getUnitByAccIdAndId(Long accId, Long id) {
        return unitRepository.findByAccommodationIdAndId(accId, id)
                .map(UnitMapper.INSTANCE::toEntity)
                .orElse(null);
    }

    @Override
    public void deleteUnitsByAccId(Long accId) {
        unitRepository.deleteByAccommodationId(accId);
    }

    @Override
    public List<UnitEntity> getUnitsByIds(List<Long> ids) {
        return unitRepository.findByIdIn(ids).stream()
                .map(UnitMapper.INSTANCE::toEntity)
                .toList();
    }
}
