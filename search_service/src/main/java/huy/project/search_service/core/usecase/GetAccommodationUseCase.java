package huy.project.search_service.core.usecase;

import huy.project.search_service.core.domain.constant.ErrorCode;
import huy.project.search_service.core.domain.dto.request.AccommodationParams;
import huy.project.search_service.core.domain.dto.response.PageInfo;
import huy.project.search_service.core.domain.entity.AccommodationEntity;
import huy.project.search_service.core.domain.exception.AppException;
import huy.project.search_service.core.port.IAccommodationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAccommodationUseCase {
    private final IAccommodationPort accommodationPort;

    public AccommodationEntity getAccById(Long id) {
        var accommodation = accommodationPort.getAccById(id);
        if (accommodation == null) {
            log.error("Accommodation with id {} not found", id);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }
        return accommodation;
    }

    public Pair<PageInfo, List<AccommodationEntity>> getAccommodations(AccommodationParams params) {
        return accommodationPort.getAccommodations(params);
    }
}
