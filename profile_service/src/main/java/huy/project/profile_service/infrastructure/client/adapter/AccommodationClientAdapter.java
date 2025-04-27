package huy.project.profile_service.infrastructure.client.adapter;

import huy.project.profile_service.core.domain.dto.response.AccommodationDto;
import huy.project.profile_service.core.port.client.IAccommodationPort;
import huy.project.profile_service.infrastructure.client.IAccommodationClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccommodationClientAdapter implements IAccommodationPort {
    IAccommodationClient accommodationClient;

    @Override
    public List<AccommodationDto> getAccommodations(List<Long> ids) {
        try {
            var response = accommodationClient.getAccommodations(ids);
            if (response.getMeta().getMessage().equals("Success") && response.getData() != null) {
                return response.getData();
            } else {
                return List.of();
            }
        } catch (Exception e) {
            log.error("Error while fetching accommodations: {}", e.getMessage());
            return List.of();
        }
    }
}
