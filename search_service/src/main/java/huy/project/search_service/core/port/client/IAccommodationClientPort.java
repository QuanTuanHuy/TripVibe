package huy.project.search_service.core.port.client;

import huy.project.search_service.core.domain.dto.request.AccommodationThumbnailParams;
import huy.project.search_service.core.domain.dto.response.AccommodationThumbnail;

import java.util.List;

public interface IAccommodationClientPort {
    List<AccommodationThumbnail> getAccommodations(AccommodationThumbnailParams params);
}
