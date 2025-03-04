package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.core.port.IAccommodationPort;
import huy.project.accommodation_service.infrastructure.repository.IAccommodationRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.AccommodationMapper;
import huy.project.accommodation_service.infrastructure.repository.model.AccommodationModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
