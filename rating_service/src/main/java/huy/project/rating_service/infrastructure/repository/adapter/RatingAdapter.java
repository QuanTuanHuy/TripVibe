package huy.project.rating_service.infrastructure.repository.adapter;

import huy.project.rating_service.core.domain.entity.RatingEntity;
import huy.project.rating_service.core.port.IRatingPort;
import huy.project.rating_service.infrastructure.repository.IRatingRepository;
import huy.project.rating_service.infrastructure.repository.mapper.RatingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingAdapter implements IRatingPort {
    private final IRatingRepository ratingRepository;

    @Override
    public RatingEntity save(RatingEntity rating) {
        var ratingModel = RatingMapper.INSTANCE.toModel(rating);
        return RatingMapper.INSTANCE.toEntity(ratingRepository.save(ratingModel));
    }

    @Override
    public RatingEntity getRatingByUnitIdAndUserId(Long unitId, Long userId) {
        return RatingMapper.INSTANCE.toEntity(ratingRepository.findByUnitIdAndUserId(unitId, userId).orElse(null));
    }
}
