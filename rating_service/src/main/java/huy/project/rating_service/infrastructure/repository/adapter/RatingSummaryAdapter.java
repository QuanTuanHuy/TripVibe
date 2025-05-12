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
    private final RatingSummaryMapper ratingSummaryMapper;

    @Override
    public void save(RatingSummaryEntity ratingSummary) {
        var model = ratingSummaryMapper.toModel(ratingSummary);
        ratingSummaryRepository.save(model);
    }

    @Override
    public void saveAll(List<RatingSummaryEntity> ratingSummaries) {
        var models = ratingSummaries.stream().map(ratingSummaryMapper::toModel).toList();
        ratingSummaryRepository.saveAll(models);
    }

    @Override
    public RatingSummaryEntity getRatingSummaryByAccId(Long accId) {
        return ratingSummaryMapper.toEntity(ratingSummaryRepository.findByAccommodationId(accId).orElse(null));
    }

    @Override
    public List<RatingSummaryEntity> getRatingSummariesByAccIds(List<Long> accIds) {
        return ratingSummaryRepository.findByAccommodationIdIn(accIds).stream()
                .map(ratingSummaryMapper::toEntity).toList();
    }

    @Override
    public List<RatingSummaryEntity> getRatingSummariesNotSynced(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return ratingSummaryRepository.findByIsSyncedWithSearchService(false, pageable).stream()
                .map(ratingSummaryMapper::toEntity).toList();
    }
}
