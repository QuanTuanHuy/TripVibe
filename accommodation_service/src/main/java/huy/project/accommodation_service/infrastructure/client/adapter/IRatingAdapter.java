package huy.project.accommodation_service.infrastructure.client.adapter;

import huy.project.accommodation_service.core.domain.dto.response.RatingSummaryDto;
import huy.project.accommodation_service.core.port.client.IRatingPort;
import huy.project.accommodation_service.infrastructure.client.IRatingClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IRatingAdapter implements IRatingPort {
    IRatingClient ratingClient;

    @Override
    public List<RatingSummaryDto> getRatingSummariesByAccIds(List<Long> ids) {
        try {
            var response = ratingClient.getRatingSummariesByAccIds(ids);
            if (response.getMeta().getMessage().equals("Success")) {
                return response.getData();
            }
        } catch (Exception e) {
            log.error("Error while calling rating service: {}", e.getMessage());
        }
        return List.of();
    }
}
