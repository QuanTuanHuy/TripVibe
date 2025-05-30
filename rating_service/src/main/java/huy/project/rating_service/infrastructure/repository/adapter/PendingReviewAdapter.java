package huy.project.rating_service.infrastructure.repository.adapter;

import com.nimbusds.jose.util.Pair;
import huy.project.rating_service.core.domain.dto.request.PendingReviewParams;
import huy.project.rating_service.core.domain.dto.response.PageInfo;
import huy.project.rating_service.core.domain.entity.PendingReviewEntity;
import huy.project.rating_service.core.port.IPendingReviewPort;
import huy.project.rating_service.infrastructure.repository.IPendingReviewRepository;
import huy.project.rating_service.infrastructure.repository.mapper.PendingReviewMapper;
import huy.project.rating_service.infrastructure.repository.specification.PendingReviewSpecification;
import huy.project.rating_service.kernel.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PendingReviewAdapter implements IPendingReviewPort {
    private final IPendingReviewRepository pendingReviewRepository;

    @Override
    public PendingReviewEntity save(PendingReviewEntity pendingReview) {
        var model = PendingReviewMapper.INSTANCE.toModel(pendingReview);
        return PendingReviewMapper.INSTANCE.toEntity(pendingReviewRepository.save(model));
    }

    @Override
    public Pair<PageInfo, List<PendingReviewEntity>> getPendingReviews(PendingReviewParams params) {
        var pageable = PageUtils.getPageable(params);
        var pendingReviews = pendingReviewRepository.findAll(PendingReviewSpecification.getPendingReviews(params), pageable);
        var pageInfo = PageUtils.getPageInfo(pendingReviews);
        return Pair.of(pageInfo, PendingReviewMapper.INSTANCE.toListEntity(pendingReviews.getContent()));
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        if (ids.size() == 1) {
            pendingReviewRepository.deleteById(ids.getFirst());
            return;
        }
        pendingReviewRepository.deleteByIdIn(ids);
    }
}
