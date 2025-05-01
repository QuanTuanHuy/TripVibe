package huy.project.accommodation_service.infrastructure.repository.adapter;

import com.nimbusds.jose.util.Pair;
import huy.project.accommodation_service.core.domain.dto.request.AmenityGroupParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import huy.project.accommodation_service.core.port.IAmenityGroupPort;
import huy.project.accommodation_service.infrastructure.repository.IAmenityGroupRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.AmenityGroupMapper;
import huy.project.accommodation_service.infrastructure.repository.model.AmenityGroupModel;
import huy.project.accommodation_service.infrastructure.repository.specification.AmenityGroupSpecification;
import huy.project.accommodation_service.kernel.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmenityGroupAdapter implements IAmenityGroupPort {
    private final IAmenityGroupRepository amenityGroupRepository;

    @Override
    public AmenityGroupEntity save(AmenityGroupEntity amenityGroup) {
        AmenityGroupModel amenityGroupModel = AmenityGroupMapper.INSTANCE.toModel(amenityGroup);
        return AmenityGroupMapper.INSTANCE.toEntity(amenityGroupRepository.save(amenityGroupModel));
    }

    @Override
    public AmenityGroupEntity getAmenityGroupById(Long id) {
        return AmenityGroupMapper.INSTANCE.toEntity(amenityGroupRepository.findById(id).orElse(null));
    }

    @Override
    public AmenityGroupEntity getAmenityGroupByName(String name) {
        return amenityGroupRepository.findByName(name)
                .map(AmenityGroupMapper.INSTANCE::toEntity)
                .orElse(null);
    }

    @Override
    public Pair<PageInfo, List<AmenityGroupEntity>> getAllAmenityGroups(AmenityGroupParams params) {
        var pageable = PageUtils.getPageable(params);

        var result = amenityGroupRepository.findAll(AmenityGroupSpecification.getAmenityGroups(params), pageable);

        var pageInfo = PageUtils.getPageInfo(result);

        return Pair.of(pageInfo, AmenityGroupMapper.INSTANCE.toListAmenityGroup(result.getContent()));
    }

    @Override
    public void deleteAmenityGroupById(Long id) {
        amenityGroupRepository.deleteById(id);
    }
}
