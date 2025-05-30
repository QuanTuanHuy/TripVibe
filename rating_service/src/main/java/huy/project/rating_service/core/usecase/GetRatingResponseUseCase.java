package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.entity.RatingResponseEntity;
import huy.project.rating_service.core.port.IRatingResponsePort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetRatingResponseUseCase {
    IRatingResponsePort ratingResponsePort;

    public List<RatingResponseEntity> getRatingResponsesByRatingIds(List<Long> ratingIds) {
        return ratingResponsePort.getRatingResponsesByRatingIds(ratingIds);
    }
}
