package huy.project.inventory_service.infrastructure.repository.adapter;

import huy.project.inventory_service.core.domain.entity.Accommodation;
import huy.project.inventory_service.core.port.IAccommodationPort;
import huy.project.inventory_service.infrastructure.repository.IAccommodationRepository;
import huy.project.inventory_service.infrastructure.repository.mapper.AccommodationMapper;
import huy.project.inventory_service.infrastructure.repository.model.AccommodationModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccommodationAdapter implements IAccommodationPort {

    private final IAccommodationRepository accommodationRepository;

    @Override
    public Accommodation save(Accommodation accommodation) {
        AccommodationModel model = AccommodationMapper.INSTANCE.toModel(accommodation);
        AccommodationModel savedModel = accommodationRepository.save(model);
        return AccommodationMapper.INSTANCE.toEntity(savedModel);
    }

    @Override
    public Accommodation getAccommodationById(Long id) {
        return accommodationRepository.findById(id)
                .map(AccommodationMapper.INSTANCE::toEntity)
                .orElse(null);
    }

    @Override
    public List<Accommodation> findAll() {
        List<AccommodationModel> models = accommodationRepository.findAll();
        return AccommodationMapper.INSTANCE.toEntityList(models);
    }

    @Override
    public List<Accommodation> saveAll(List<Accommodation> accommodations) {
        List<AccommodationModel> models = AccommodationMapper.INSTANCE.toModelList(accommodations);
        List<AccommodationModel> savedModels = accommodationRepository.saveAll(models);
        return AccommodationMapper.INSTANCE.toEntityList(savedModels);
    }

    @Override
    public void deleteAccommodationById(Long id) {
        accommodationRepository.deleteById(id);
    }
}
