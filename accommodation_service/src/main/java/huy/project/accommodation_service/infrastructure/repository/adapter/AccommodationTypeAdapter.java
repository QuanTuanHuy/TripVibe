package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.dto.request.AccommodationTypeParams;
import huy.project.accommodation_service.core.domain.entity.AccommodationTypeEntity;
import huy.project.accommodation_service.core.port.IAccommodationTypePort;
import huy.project.accommodation_service.infrastructure.repository.IAccommodationTypeRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.AccommodationTypeMapper;
import huy.project.accommodation_service.infrastructure.repository.model.AccommodationTypeModel;
import huy.project.accommodation_service.infrastructure.repository.specification.AccommodationTypeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationTypeAdapter implements IAccommodationTypePort {
    private final IAccommodationTypeRepository accommodationTypeRepository;

    @Override
    public AccommodationTypeEntity save(AccommodationTypeEntity accommodationType) {
        AccommodationTypeModel accommodationTypeModel = AccommodationTypeMapper.INSTANCE.toModel(accommodationType);
        return AccommodationTypeMapper.INSTANCE.toEntity(
                accommodationTypeRepository.save(accommodationTypeModel));
    }

    @Override
    public AccommodationTypeEntity getAccommodationTypeByName(String name) {
        return accommodationTypeRepository.findByName(name)
                .map(AccommodationTypeMapper.INSTANCE::toEntity)
                .orElse(null);
    }

    @Override
    public AccommodationTypeEntity getAccommodationTypeById(Long id) {
        return accommodationTypeRepository.findById(id)
                .map(AccommodationTypeMapper.INSTANCE::toEntity).orElse(null);
    }

    @Override
    public List<AccommodationTypeEntity> getAccommodationTypes(AccommodationTypeParams params) {
        return AccommodationTypeMapper.INSTANCE.toListEntity(accommodationTypeRepository
                .findAll(AccommodationTypeSpecification.getAccommodationTypes(params)));
    }
}
