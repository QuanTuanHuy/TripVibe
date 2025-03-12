package huy.project.profile_service.core.usecase;

import huy.project.profile_service.core.domain.constant.ErrorCode;
import huy.project.profile_service.core.domain.constant.MemberLevel;
import huy.project.profile_service.core.domain.entity.TouristEntity;
import huy.project.profile_service.core.domain.exception.AppException;
import huy.project.profile_service.core.port.ITouristPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateTouristUseCase {
    private final ITouristPort touristPort;

    @Transactional(rollbackFor = Exception.class)
    public TouristEntity createTourist(Long userId, String email) {
        TouristEntity existedTourist = touristPort.getTouristById(userId);
        if (existedTourist != null) {
            log.error("Tourist existed with userId: {}", userId);
            throw new AppException(ErrorCode.TOURIST_ALREADY_EXISTS);
        }

        TouristEntity tourist = TouristEntity.builder()
                .id(userId).email(email)
                .memberLevel(MemberLevel.LEVEL_1.getLevel())
                .isActive(true)
                .build();
        return touristPort.save(tourist);
    }
}
