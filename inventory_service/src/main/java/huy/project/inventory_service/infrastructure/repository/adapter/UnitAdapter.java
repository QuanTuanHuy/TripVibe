package huy.project.inventory_service.infrastructure.repository.adapter;

import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.port.IUnitPort;
import huy.project.inventory_service.infrastructure.repository.IUnitRepository;
import huy.project.inventory_service.infrastructure.repository.mapper.UnitMapper;
import huy.project.inventory_service.infrastructure.repository.model.UnitModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnitAdapter implements IUnitPort {

    private final IUnitRepository unitRepository;

    @Override
    public Unit getUnitById(Long id) {
        return unitRepository.findById(id)
                .map(UnitMapper.INSTANCE::toEntity)
                .orElse(null);
    }

    @Override
    public List<Unit> getUnitsByAccommodationId(Long accommodationId) {
        List<UnitModel> models = unitRepository.findByAccommodationId(accommodationId);
        return UnitMapper.INSTANCE.toEntityList(models);
    }

    @Override
    public Unit save(Unit unit) {
        UnitModel model = UnitMapper.INSTANCE.toModel(unit);
        UnitModel savedModel = unitRepository.save(model);
        return UnitMapper.INSTANCE.toEntity(savedModel);
    }

    @Override
    public List<Unit> saveAll(List<Unit> units) {
        List<UnitModel> models = UnitMapper.INSTANCE.toModelList(units);
        List<UnitModel> savedModels = unitRepository.saveAll(models);
        return UnitMapper.INSTANCE.toEntityList(savedModels);
    }

    @Override
    @Transactional
    public void deleteUnitById(Long id) {
        unitRepository.deleteById(id);
    }
}
