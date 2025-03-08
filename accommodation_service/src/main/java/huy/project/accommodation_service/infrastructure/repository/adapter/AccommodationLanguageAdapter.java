package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.AccommodationLanguageEntity;
import huy.project.accommodation_service.core.port.IAccommodationLanguagePort;
import huy.project.accommodation_service.infrastructure.repository.IAccommodationLanguageRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.AccommodationLanguageMapper;
import huy.project.accommodation_service.infrastructure.repository.model.AccommodationLanguageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationLanguageAdapter implements IAccommodationLanguagePort {
    private final IAccommodationLanguageRepository accLanguageRepository;

    @Override
    public List<AccommodationLanguageEntity> saveAll(List<AccommodationLanguageEntity> accLanguages) {
        List<AccommodationLanguageModel> accLanguageModels = AccommodationLanguageMapper.INSTANCE.toListModel(accLanguages);
        return AccommodationLanguageMapper.INSTANCE.toListEntity(accLanguageRepository.saveAll(accLanguageModels));
    }

    @Override
    public List<AccommodationLanguageEntity> getAccLanguagesByAccId(Long accId) {
        return AccommodationLanguageMapper.INSTANCE.toListEntity(
                accLanguageRepository.findByAccommodationId(accId)
        );
    }

    @Override
    public void deleteAccLanguagesByAccId(Long accId) {
        accLanguageRepository.deleteByAccommodationId(accId);
    }
}
