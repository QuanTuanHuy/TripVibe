package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.dto.request.AccommodationParams;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.core.port.IAccommodationPort;
import huy.project.accommodation_service.infrastructure.repository.IAccommodationRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.AccommodationMapper;
import huy.project.accommodation_service.infrastructure.repository.model.AccommodationModel;
import huy.project.accommodation_service.infrastructure.repository.specification.AccommodationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationAdapter implements IAccommodationPort {
    private final IAccommodationRepository accommodationRepository;

    @Override
    public AccommodationEntity save(AccommodationEntity accommodation) {
        AccommodationModel accommodationModel = AccommodationMapper.INSTANCE.toModel(accommodation);
        return AccommodationMapper.INSTANCE.toEntity(accommodationRepository.save(accommodationModel));
    }

    @Override
    public AccommodationEntity getAccommodationByName(String name) {
        return accommodationRepository.findByName(name)
                .map(AccommodationMapper.INSTANCE::toEntity)
                .orElse(null);
    }

    @Override
    public AccommodationEntity getAccommodationById(Long id) {
        return accommodationRepository.findById(id)
                .map(AccommodationMapper.INSTANCE::toEntity).orElse(null);
    }

    @Override
    public List<AccommodationEntity> getAccommodations(AccommodationParams params) {
        return AccommodationMapper.INSTANCE.toListEntity(
                accommodationRepository.findAll(AccommodationSpecification.getAccommodations(params)));
    }

    @Override
    public void deleteAccommodationById(Long id) {
        accommodationRepository.deleteById(id);
    }

    @Override
    public List<AccommodationEntity> getAccommodationsByIds(List<Long> ids) {
        return AccommodationMapper.INSTANCE.toListEntity(accommodationRepository.findByIdIn(ids));
    }
}
