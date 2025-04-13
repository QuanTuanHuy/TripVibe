package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.entity.RatingSummaryEntity;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.port.IRatingSummaryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateRatingSummaryUseCase {
    private final IRatingSummaryPort ratingSummaryPort;

    @Transactional(rollbackFor = Exception.class)
    public void updateRatingSummaries(List<RatingSummaryEntity> ratingSummaries) {
        try {
            ratingSummaryPort.saveAll(ratingSummaries);
        } catch (Exception e) {
            log.error("Error while updating rating summaries: {}", e.getMessage());
            throw new AppException(ErrorCode.UPDATE_RATING_SUMMARY_FAILED);
        }
    }
}
