package huy.project.profile_service.core.port;

import huy.project.profile_service.core.domain.dto.request.ViewHistoryParams;
import huy.project.profile_service.core.domain.dto.response.PageInfo;
import huy.project.profile_service.core.domain.entity.ViewHistoryEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IViewHistoryPort {
    ViewHistoryEntity save(ViewHistoryEntity viewHistory);
    ViewHistoryEntity getViewHistoryByTouristIdAndAccId(Long touristId, Long accId);
    Pair<PageInfo, List<ViewHistoryEntity>> getViewHistories(ViewHistoryParams params);
}
