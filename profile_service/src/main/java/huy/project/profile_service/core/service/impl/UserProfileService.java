package huy.project.profile_service.core.service.impl;

import huy.project.profile_service.core.domain.dto.request.UserProfileDto;
import huy.project.profile_service.core.service.IUserProfileService;
import huy.project.profile_service.core.usecase.GetUserProfileUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService implements IUserProfileService {
    private final GetUserProfileUseCase getUserProfileUseCase;

    @Override
    public UserProfileDto getUserProfile(Long userId) {
        return getUserProfileUseCase.getUserProfile(userId);
    }
}
