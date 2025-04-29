package huy.project.search_service.core.port;

import huy.project.search_service.core.domain.dto.request.AccommodationParams;
import huy.project.search_service.core.domain.dto.response.PageInfo;
import huy.project.search_service.core.domain.entity.AccommodationEntity;
import org.springframework.data.util.Pair;

import java.util.Date;
import java.util.List;

public interface IAccommodationPort {
    Pair<PageInfo, List<AccommodationEntity>> getAccommodations(AccommodationParams params);
    void updateAvailability(Long accommodationId, Long unitId, Date startDate, Date endDate, Integer delta);
    AccommodationEntity save(AccommodationEntity accommodation);
    AccommodationEntity getAccById(Long accId);
    void deleteAccById(Long accId);
}
