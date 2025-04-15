package huy.project.rating_service.infrastructure.repository.adapter;

import huy.project.rating_service.core.domain.dto.request.RatingParams;
import huy.project.rating_service.core.domain.dto.response.PageInfo;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import huy.project.rating_service.core.port.IRatingPort;
import huy.project.rating_service.infrastructure.repository.IRatingRepository;
import huy.project.rating_service.infrastructure.repository.mapper.RatingMapper;
import huy.project.rating_service.infrastructure.repository.specification.RatingSpecification;
import huy.project.rating_service.kernel.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public Pair<PageInfo, List<RatingEntity>> getAllRatings(RatingParams params) {
        var pageable = PageUtils.getPageable(params);
        var ratings = ratingRepository.findAll(RatingSpecification.getAllRatings(params), pageable);
        var pageInfo = PageUtils.getPageInfo(ratings);
        return Pair.of(pageInfo, RatingMapper.INSTANCE.toListEntity(ratings.getContent()));
    }

    @Override
    public RatingEntity getRatingById(Long id) {
        return RatingMapper.INSTANCE.toEntity(ratingRepository.findById(id).orElse(null));
    }
}
