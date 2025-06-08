package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.dto.request.BedTypeParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.BedTypeEntity;
import huy.project.accommodation_service.core.port.IBedTypePort;
import huy.project.accommodation_service.infrastructure.repository.IBedTypeRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.BedTypeMapper;
import huy.project.accommodation_service.infrastructure.repository.model.BedTypeModel;
import huy.project.accommodation_service.infrastructure.repository.specification.BedTypeSpecification;
import huy.project.accommodation_service.kernel.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BedTypeAdapter implements IBedTypePort {
    private final IBedTypeRepository bedTypeRepository;

    @Override
    public BedTypeEntity save(BedTypeEntity bedType) {
        BedTypeModel bedTypeModel = BedTypeMapper.INSTANCE.toModel(bedType);
        return BedTypeMapper.INSTANCE.toEntity(bedTypeRepository.save(bedTypeModel));
    }

    @Override
    public BedTypeEntity getBedTypeByName(String name) {
        return bedTypeRepository.findByName(name)
                .map(BedTypeMapper.INSTANCE::toEntity)
                .orElse(null);
    }

    @Override
    public Pair<PageInfo, List<BedTypeEntity>> getBedTypes(BedTypeParams params) {
        var pageable = PageUtils.getPageable(params);

        var result = bedTypeRepository.findAll(BedTypeSpecification.getBedTypes(params), pageable);

        var pageInfo = PageUtils.getPageInfo(result);

        return Pair.of(pageInfo, BedTypeMapper.INSTANCE.toListEntity(result.getContent()));
    }

    @Override
    public List<BedTypeEntity> getBedTypesByIds(List<Long> ids) {
        return BedTypeMapper.INSTANCE.toListEntity(bedTypeRepository.findByIdIn(ids));
    }

    @Override
    public long countAll() {
        return bedTypeRepository.count();
    }
}
