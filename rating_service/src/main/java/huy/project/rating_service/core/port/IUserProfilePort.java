package huy.project.rating_service.core.port;

import huy.project.rating_service.core.domain.dto.response.UserProfileDto;

import java.util.List;

public interface IUserProfilePort {
    List<UserProfileDto> getUserProfilesByIds(List<Long> userIds);
}
