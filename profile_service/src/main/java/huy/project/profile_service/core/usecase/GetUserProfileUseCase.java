package huy.project.profile_service.core.usecase;

import huy.project.profile_service.core.domain.constant.CacheConstant;
import huy.project.profile_service.core.domain.constant.ErrorCode;
import huy.project.profile_service.core.domain.dto.request.UserProfileDto;
import huy.project.profile_service.core.domain.exception.AppException;
import huy.project.profile_service.core.domain.mapper.UserProfileMapper;
import huy.project.profile_service.core.port.ICachePort;
import huy.project.profile_service.kernel.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserProfileUseCase {
    private final GetTouristUseCase getTouristUseCase;
    private final ICachePort cachePort;

    public UserProfileDto getUserProfile(Long userId) {
        var cacheKey = CacheUtils.buildCacheKeyGetUserProfileById(userId);
        var cachedUserProfile = cachePort.getFromCache(cacheKey, UserProfileDto.class);
        if (cachedUserProfile != null) {
            return cachedUserProfile;
        }

        // need to get the owner of accommodation
        try {
            var tourist = getTouristUseCase.getTouristById(userId);
            var userProfile = UserProfileMapper.INSTANCE.toUserProfileDto(tourist);

            cachePort.setToCache(cacheKey, userProfile, CacheConstant.DEFAULT_TTL);

            return userProfile;
        } catch (Exception e) {
            log.error("getUserProfile: error when get user profile, userId: {}", userId, e);
            throw new AppException(ErrorCode.USER_PROFILE_NOT_FOUND);
        }
    }
}
