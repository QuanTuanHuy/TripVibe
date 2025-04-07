package huy.project.rating_service.infrastructure.client.adapter;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.dto.response.UserProfileDto;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.port.IUserProfilePort;
import huy.project.rating_service.infrastructure.client.IUserProfileClient;
import huy.project.rating_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IUserProfileClientAdapter implements IUserProfilePort {
    private final IUserProfileClient userProfileClient;

    @Override
    public List<UserProfileDto> getUserProfilesByIds(List<Long> userIds) {
        try {
            Resource<List<UserProfileDto>> response = userProfileClient.getUserProfilesByIds(userIds);
            if (response.getMeta().getMessage().equals("Success") && response.getData() != null) {
                return response.getData();
            } else {
                return List.of();
            }
        } catch (Exception e) {
            log.error("error when calling profile service: ", e);
            throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }
}
