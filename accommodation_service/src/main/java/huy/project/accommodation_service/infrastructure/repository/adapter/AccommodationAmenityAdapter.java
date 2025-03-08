package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.AccommodationAmenityEntity;
import huy.project.accommodation_service.core.port.IAccommodationAmenityPort;
import huy.project.accommodation_service.infrastructure.repository.IAccommodationAmenityRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.AccommodationAmenityMapper;
import huy.project.accommodation_service.infrastructure.repository.model.AccommodationAmenityModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationAmenityAdapter implements IAccommodationAmenityPort {
    private final IAccommodationAmenityRepository accommodationAmenityRepository;

    @Override
    public List<AccommodationAmenityEntity> saveAll(List<AccommodationAmenityEntity> accommodationAmenities) {
        List<AccommodationAmenityModel> accAmenityModels = AccommodationAmenityMapper.INSTANCE
                .toListModel(accommodationAmenities);
        return AccommodationAmenityMapper.INSTANCE.toListEntity(accommodationAmenityRepository.saveAll(accAmenityModels));
    }

    @Override
    public List<AccommodationAmenityEntity> getAccAmenitiesByAccId(Long accId) {
        return AccommodationAmenityMapper.INSTANCE.toListEntity(
                accommodationAmenityRepository.findByAccommodationId(accId));
    }

    @Override
    public void deleteByAccIdAndAmenities(Long accId, List<Long> amenityIds) {
        accommodationAmenityRepository.deleteByAccommodationIdAndAmenityIdIn(accId, amenityIds);
    }


}
