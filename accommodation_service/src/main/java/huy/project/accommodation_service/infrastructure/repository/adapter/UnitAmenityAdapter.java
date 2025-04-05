package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.UnitAmenityEntity;
import huy.project.accommodation_service.core.port.IUnitAmenityPort;
import huy.project.accommodation_service.infrastructure.repository.IUnitAmenityRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.UnitAmenityMapper;
import huy.project.accommodation_service.infrastructure.repository.model.UnitAmenityModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitAmenityAdapter implements IUnitAmenityPort {
    private final IUnitAmenityRepository unitAmenityRepository;

    @Override
    public List<UnitAmenityEntity> saveAll(List<UnitAmenityEntity> unitAmenities) {
        List<UnitAmenityModel> unitAmenityModels = UnitAmenityMapper.INSTANCE.toListModel(unitAmenities);
        return UnitAmenityMapper.INSTANCE.toListEntity(unitAmenityRepository.saveAll(unitAmenityModels));
    }

    @Override
    public List<UnitAmenityEntity> getUnitAmenitiesByUnitIds(List<Long> unitIds) {
        return UnitAmenityMapper.INSTANCE.toListEntity(unitAmenityRepository.findByUnitIdIn(unitIds));
    }

    @Override
    public List<UnitAmenityEntity> getUnitAmenitiesByUnitId(Long unitId) {
        return UnitAmenityMapper.INSTANCE.toListEntity(unitAmenityRepository.findByUnitId(unitId));
    }

    @Override
    public void deleteByUnitIdAndAmenityIdIn(Long unitId, List<Long> amenityIds) {
        unitAmenityRepository.deleteByUnitIdAndAmenityIdIn(unitId, amenityIds);
    }
}
