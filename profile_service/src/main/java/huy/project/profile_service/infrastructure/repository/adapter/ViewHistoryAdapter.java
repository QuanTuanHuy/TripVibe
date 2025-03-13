package huy.project.profile_service.infrastructure.repository.adapter;

import huy.project.profile_service.core.domain.dto.request.ViewHistoryParams;
import huy.project.profile_service.core.domain.dto.response.PageInfo;
import huy.project.profile_service.core.domain.entity.ViewHistoryEntity;
import huy.project.profile_service.core.port.IViewHistoryPort;
import huy.project.profile_service.infrastructure.repository.IViewHistoryRepository;
import huy.project.profile_service.infrastructure.repository.mapper.ViewHistoryMapper;
import huy.project.profile_service.infrastructure.repository.model.ViewHistoryModel;
import huy.project.profile_service.infrastructure.repository.specification.ViewHistorySpecification;
import huy.project.profile_service.kernel.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewHistoryAdapter implements IViewHistoryPort {
    private final IViewHistoryRepository viewHistoryRepository;

    @Override
    public ViewHistoryEntity save(ViewHistoryEntity viewHistory) {
        ViewHistoryModel viewHistoryModel = ViewHistoryMapper.INSTANCE.toModel(viewHistory);
        return ViewHistoryMapper.INSTANCE.toEntity(viewHistoryRepository.save(viewHistoryModel));
    }

    @Override
    public ViewHistoryEntity getViewHistoryByTouristIdAndAccId(Long touristId, Long accId) {
        return ViewHistoryMapper.INSTANCE.toEntity(
                viewHistoryRepository.findByTouristIdAndAccommodationId(touristId, accId).orElse(null)
        );
    }

    @Override
    public Pair<PageInfo, List<ViewHistoryEntity>> getViewHistories(ViewHistoryParams params) {
        Pageable pageable = PageUtils.getPageable(params);

        var result = viewHistoryRepository.findAll(ViewHistorySpecification.getViewHistories(params), pageable);
        var pageInfo = PageUtils.getPageInfo(result);

        return Pair.of(pageInfo, ViewHistoryMapper.INSTANCE.toListEntity(result.getContent()));
    }
}
