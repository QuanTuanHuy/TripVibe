package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.UnitPriceGroupEntity;
import huy.project.accommodation_service.core.port.IUnitPriceGroupPort;
import huy.project.accommodation_service.infrastructure.repository.IUnitPriceGroupRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.UnitPriceGroupMapper;
import huy.project.accommodation_service.infrastructure.repository.model.UnitPriceGroupModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitPriceGroupAdapter implements IUnitPriceGroupPort {
    private final IUnitPriceGroupRepository unitPriceGroupRepository;

    @Override
    public List<UnitPriceGroupEntity> saveAll(List<UnitPriceGroupEntity> priceGroups) {
        List<UnitPriceGroupModel> priceGroupModels = UnitPriceGroupMapper.INSTANCE.toListModel(priceGroups);
        return UnitPriceGroupMapper.INSTANCE.toListEntity(unitPriceGroupRepository.saveAll(priceGroupModels));
    }

    @Override
    public List<UnitPriceGroupEntity> getPriceGroupsByUnitIds(List<Long> unitIds) {
        return UnitPriceGroupMapper.INSTANCE.toListEntity(
                unitPriceGroupRepository.findByUnitIdIn(unitIds)
        );
    }
}
