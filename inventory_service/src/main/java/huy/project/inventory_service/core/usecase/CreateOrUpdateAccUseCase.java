package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.dto.request.SyncAccommodationDto;
import huy.project.inventory_service.core.domain.entity.Accommodation;
import huy.project.inventory_service.core.port.IAccommodationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateOrUpdateAccUseCase {
    private final IAccommodationPort accommodationPort;

    @Transactional(rollbackFor = Exception.class)
    public Accommodation createOrUpdate(SyncAccommodationDto request) {
        var existing = accommodationPort.getAccommodationById(request.getId());
        if (existing != null) {
            existing.setName(request.getName());
            existing.setAddress(request.getAddress());
            existing.setDescription(request.getDescription());
            return accommodationPort.save(existing);
        } else {
            var accommodation = Accommodation.builder()
                    .id(request.getId())
                    .name(request.getName())
                    .address(request.getAddress())
                    .description(request.getDescription())
                    .build();
            return accommodationPort.save(accommodation);
        }
    }
}
