package huy.project.rating_service.infrastructure.repository.adapter;

import huy.project.rating_service.core.domain.entity.RatingSummaryEntity;
import huy.project.rating_service.core.port.IRatingSummaryPort;
import huy.project.rating_service.infrastructure.repository.IRatingSummaryRepository;
import huy.project.rating_service.infrastructure.repository.mapper.RatingSummaryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingSummaryAdapter implements IRatingSummaryPort {
    private final IRatingSummaryRepository ratingSummaryRepository;

    @Override
    public void save(RatingSummaryEntity ratingSummary) {
        var model = RatingSummaryMapper.INSTANCE.toModel(ratingSummary);
        ratingSummaryRepository.save(model);
    }

    @Override
    public void saveAll(List<RatingSummaryEntity> ratingSummaries) {
        var models = RatingSummaryMapper.INSTANCE.toListModel(ratingSummaries);
        ratingSummaryRepository.saveAll(models);
    }

    @Override
    public RatingSummaryEntity getRatingSummaryByAccId(Long accId) {
        return RatingSummaryMapper.INSTANCE.toEntity(ratingSummaryRepository.findByAccommodationId(accId).orElse(null));
    }

    @Override
    public List<RatingSummaryEntity> getRatingSummariesByAccIds(List<Long> accIds) {
        return RatingSummaryMapper.INSTANCE.toListEntity(ratingSummaryRepository.findByAccommodationIdIn(accIds));
    }

    @Override
    public List<RatingSummaryEntity> getRatingSummariesNotSynced(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return RatingSummaryMapper.INSTANCE.toListEntity(
                ratingSummaryRepository.findByIsSyncedWithSearchService(false, pageable));
    }
}
