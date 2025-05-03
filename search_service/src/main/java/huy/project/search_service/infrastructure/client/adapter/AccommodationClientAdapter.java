package huy.project.search_service.infrastructure.client.adapter;

import huy.project.search_service.core.domain.constant.ErrorCode;
import huy.project.search_service.core.domain.dto.request.AccommodationThumbnailParams;
import huy.project.search_service.core.domain.dto.response.AccommodationThumbnail;
import huy.project.search_service.core.domain.exception.AppException;
import huy.project.search_service.core.port.client.IAccommodationClientPort;
import huy.project.search_service.infrastructure.client.IAccommodationClient;
import huy.project.search_service.ui.resource.Resource;
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
public class AccommodationClientAdapter implements IAccommodationClientPort {
    IAccommodationClient accommodationClient;

    @Override
    public List<AccommodationThumbnail> getAccommodations(AccommodationThumbnailParams params) {
        try {
            Resource<List<AccommodationThumbnail>> response = accommodationClient.getAccommodationsThumbnail(
                    params.getIds(),
                    params.getStartDate(),
                    params.getEndDate(),
                    params.getGuestCount()
            );
            if (response.getMeta().getMessage().equals("Success")) {
                return response.getData();
            } else {
                throw new AppException(ErrorCode.GET_ACCOMMODATION_FAILED);
            }
        } catch (Exception e) {
            log.error("Error while calling accommodation service: {}", e.getMessage());
            throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }
}
