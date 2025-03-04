package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.dto.request.UnitNameParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.UnitNameEntity;
import huy.project.accommodation_service.core.port.IUnitNamePort;
import huy.project.accommodation_service.infrastructure.repository.IUnitNameRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.UnitNameMapper;
import huy.project.accommodation_service.infrastructure.repository.model.UnitNameModel;
import huy.project.accommodation_service.infrastructure.repository.specification.UnitNameSpecification;
import huy.project.accommodation_service.kernel.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitNameAdapter implements IUnitNamePort {
    private final IUnitNameRepository unitNameRepository;

    @Override
    public UnitNameEntity save(UnitNameEntity unitName) {
        UnitNameModel unitNameModel = UnitNameMapper.INSTANCE.toModel(unitName);
        return UnitNameMapper.INSTANCE.toEntity(unitNameRepository.save(unitNameModel));
    }

    @Override
    public UnitNameEntity getUnitNameByName(String name) {
        return unitNameRepository.findByName(name).map(UnitNameMapper.INSTANCE::toEntity).orElse(null);
    }

    @Override
    public Pair<PageInfo, List<UnitNameEntity>> getUnitNames(UnitNameParams params) {
        Pageable pageable = PageUtils.getPageable(params);

        var result = unitNameRepository.findAll(UnitNameSpecification.getUnitNames(params), pageable);
        var pageInfo = PageUtils.getPageInfo(result);

        return Pair.of(pageInfo, UnitNameMapper.INSTANCE.toListEntity(result.getContent()));
    }

    @Override
    public List<UnitNameEntity> getUnitNamesByIds(List<Long> ids) {
        return UnitNameMapper.INSTANCE.toListEntity(unitNameRepository.findByIdIn(ids));
    }

    @Override
    public UnitNameEntity getUnitNameById(Long id) {
        return unitNameRepository.findById(id).map(UnitNameMapper.INSTANCE::toEntity).orElse(null);
    }
}
