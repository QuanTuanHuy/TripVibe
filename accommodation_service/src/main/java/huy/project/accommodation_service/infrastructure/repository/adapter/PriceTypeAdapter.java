package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.PriceTypeEntity;
import huy.project.accommodation_service.core.port.IPriceTypePort;
import huy.project.accommodation_service.infrastructure.repository.IPriceTypeRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.PriceTypeMapper;
import lombok.RequiredArgsConstructor;
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
}
