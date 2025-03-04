package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.UnitPriceTypeEntity;
import huy.project.accommodation_service.core.port.IUnitPriceTypePort;
import huy.project.accommodation_service.infrastructure.repository.IUnitPriceTypeRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.UnitPriceTypeMapper;
import huy.project.accommodation_service.infrastructure.repository.model.UnitPriceTypeModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitPriceTypeAdapter implements IUnitPriceTypePort {
    private final IUnitPriceTypeRepository unitPriceTypeRepository;

    @Override
    public List<UnitPriceTypeEntity> saveAll(List<UnitPriceTypeEntity> unitPriceTypes) {
        List<UnitPriceTypeModel> unitPriceTypeModels = UnitPriceTypeMapper.INSTANCE.toListModel(unitPriceTypes);
        return UnitPriceTypeMapper.INSTANCE.toListEntity(unitPriceTypeRepository.saveAll(unitPriceTypeModels));
    }

    @Override
    public List<UnitPriceTypeEntity> getUnitPricesByUnitIds(List<Long> unitIds) {
        return UnitPriceTypeMapper.INSTANCE.toListEntity(unitPriceTypeRepository.findByUnitIdIn(unitIds));
    }
}
