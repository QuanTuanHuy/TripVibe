package huy.project.rating_service.core.usecase;

import huy.project.rating_service.core.domain.constant.CacheConstant;
import huy.project.rating_service.core.domain.dto.response.UserProfileDto;
import huy.project.rating_service.core.port.ICachePort;
import huy.project.rating_service.core.port.IUserProfilePort;
import huy.project.rating_service.kernel.utils.CacheUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetUserProfileUseCase {
    ICachePort cachePort;
    IUserProfilePort userProfilePort;

    public List<UserProfileDto> getUserProfilesByIds(List<Long> userIds) {
        Set<Long> userIdSet = new HashSet<>(userIds);
        List<Long> userIdCacheMiss = new ArrayList<>();
        List<UserProfileDto> userProfiles = new ArrayList<>();

        userIdSet.stream()
                .parallel()
                .forEach(userId -> {
                    String cacheKey = CacheUtils.buildCacheKeyGetUserProfileById(userId);
                    var cachedResult = cachePort.getFromCache(cacheKey, UserProfileDto.class);
                    if (cachedResult != null) {
                        userProfiles.add(cachedResult);
                    } else {
                        userIdCacheMiss.add(userId);
                    }
                });

        if (!CollectionUtils.isEmpty(userIdCacheMiss)) {
            List<UserProfileDto> fetchedUserProfiles = userProfilePort.getUserProfilesByIds(userIdCacheMiss);
            userProfiles.addAll(fetchedUserProfiles);

            // Cache the fetched user profiles
            for (UserProfileDto userProfile : fetchedUserProfiles) {
                String cacheKey = CacheUtils.buildCacheKeyGetUserProfileById(userProfile.getUserId());
                cachePort.setToCache(cacheKey, userProfile, CacheConstant.DEFAULT_TTL);
            }
        }

        return userProfiles;
    }
}
