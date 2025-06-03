package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.dto.request.PriceTypeParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.PriceTypeEntity;
import huy.project.accommodation_service.core.port.IPriceTypePort;
import huy.project.accommodation_service.infrastructure.repository.IPriceTypeRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.PriceTypeMapper;
import huy.project.accommodation_service.infrastructure.repository.model.PriceTypeModel;
import huy.project.accommodation_service.infrastructure.repository.specification.PriceTypeSpecification;
import huy.project.accommodation_service.kernel.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceTypeAdapter implements IPriceTypePort {
    private final IPriceTypeRepository priceTypeRepository;

    @Override
    public PriceTypeEntity getPriceTypeByName(String name) {
        return priceTypeRepository.findByName(name)
                .map(PriceTypeMapper.INSTANCE::toEntity).orElse(null);
    }

    @Override
    public PriceTypeEntity getPriceTypeById(Long id) {
        return priceTypeRepository.findById(id)
                .map(PriceTypeMapper.INSTANCE::toEntity).orElse(null);
    }

    @Override
    public List<PriceTypeEntity> getAllPriceTypes() {
        return PriceTypeMapper.INSTANCE.toListEntity(priceTypeRepository.findAll());
    }

    @Override
    public PriceTypeEntity save(PriceTypeEntity priceType) {
        PriceTypeModel model = PriceTypeMapper.INSTANCE.toModel(priceType);
        return PriceTypeMapper.INSTANCE.toEntity(priceTypeRepository.save(model));
    }

    @Override
    public long countAll() {
        return priceTypeRepository.count();
    }

    @Override
    public Pair<PageInfo, List<PriceTypeEntity>> getPriceTypes(PriceTypeParams params) {
        Pageable pageable = PageUtils.getPageable(params);
        var result = priceTypeRepository.findAll(
                PriceTypeSpecification.getPriceTypes(params),
                pageable
        );
        var pageInfo = PageUtils.getPageInfo(result);
        return Pair.of(pageInfo, PriceTypeMapper.INSTANCE.toListEntity(result.getContent()));
    }
}
