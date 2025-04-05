package huy.project.profile_service.core.service;

import huy.project.profile_service.core.domain.dto.request.UserProfileDto;

public interface IUserProfileService {
    UserProfileDto getUserProfile(Long userId);
}
