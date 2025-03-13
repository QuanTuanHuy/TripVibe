package huy.project.profile_service.core.usecase;

import huy.project.profile_service.core.domain.dto.request.ViewHistoryParams;
import huy.project.profile_service.core.domain.dto.response.PageInfo;
import huy.project.profile_service.core.domain.entity.ViewHistoryEntity;
import huy.project.profile_service.core.port.IViewHistoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetViewHistoryUseCase {
    private final IViewHistoryPort viewHistoryPort;

    public Pair<PageInfo, List<ViewHistoryEntity>> getViewHistories(ViewHistoryParams params) {
        return viewHistoryPort.getViewHistories(params);
    }
}
