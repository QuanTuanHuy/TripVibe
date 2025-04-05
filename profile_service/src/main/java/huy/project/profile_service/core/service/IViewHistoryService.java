package huy.project.profile_service.core.service;

import huy.project.profile_service.core.domain.dto.kafka.CreateViewHistoryMessage;
import huy.project.profile_service.core.domain.dto.request.ViewHistoryParams;
import huy.project.profile_service.core.domain.dto.response.PageInfo;
import huy.project.profile_service.core.domain.entity.ViewHistoryEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IViewHistoryService {
    ViewHistoryEntity createViewHistory(CreateViewHistoryMessage message);
    Pair<PageInfo, List<ViewHistoryEntity>> getViewHistories(ViewHistoryParams params);
}
