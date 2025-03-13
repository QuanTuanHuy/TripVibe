package huy.project.profile_service.core.service.impl;

import huy.project.profile_service.core.domain.dto.kafka.CreateViewHistoryMessage;
import huy.project.profile_service.core.domain.dto.request.ViewHistoryParams;
import huy.project.profile_service.core.domain.dto.response.PageInfo;
import huy.project.profile_service.core.domain.entity.ViewHistoryEntity;
import huy.project.profile_service.core.service.IViewHistoryService;
import huy.project.profile_service.core.usecase.CreateViewHistoryUseCase;
import huy.project.profile_service.core.usecase.GetViewHistoryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewHistoryService implements IViewHistoryService {
    private final CreateViewHistoryUseCase createViewHistoryUseCase;
    private final GetViewHistoryUseCase getViewHistoryUseCase;

    @Override
    public ViewHistoryEntity createViewHistory(CreateViewHistoryMessage message) {
        return createViewHistoryUseCase.createViewHistory(message);
    }

    @Override
    public Pair<PageInfo, List<ViewHistoryEntity>> getViewHistories(ViewHistoryParams params) {
        return getViewHistoryUseCase.getViewHistories(params);
    }
}
