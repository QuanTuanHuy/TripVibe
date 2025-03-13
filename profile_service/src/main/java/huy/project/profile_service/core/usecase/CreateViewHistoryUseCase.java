package huy.project.profile_service.core.usecase;

import huy.project.profile_service.core.domain.dto.kafka.CreateViewHistoryMessage;
import huy.project.profile_service.core.domain.entity.ViewHistoryEntity;
import huy.project.profile_service.core.domain.mapper.ViewHistoryMapper;
import huy.project.profile_service.core.port.IViewHistoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateViewHistoryUseCase {
    private final IViewHistoryPort viewHistoryPort;
    private final GetTouristUseCase getTouristUseCase;

    @Transactional(rollbackFor = Exception.class)
    public ViewHistoryEntity createViewHistory(CreateViewHistoryMessage message) {
        // check existed tourist
        getTouristUseCase.getTouristById(message.getTouristId());

        ViewHistoryEntity existedViewHistory = viewHistoryPort
                .getViewHistoryByTouristIdAndAccId(message.getTouristId(), message.getAccommodationId());
        if (existedViewHistory != null) {
            existedViewHistory.setTimestamp(message.getTimestamp());
            return viewHistoryPort.save(existedViewHistory);
        }

        ViewHistoryEntity viewHistory = ViewHistoryMapper.INSTANCE.toEntity(message);
        return viewHistoryPort.save(viewHistory);
    }
}
