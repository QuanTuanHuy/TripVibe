package huy.project.profile_service.core.usecase;

import huy.project.profile_service.core.domain.constant.ErrorCode;
import huy.project.profile_service.core.domain.entity.FriendEntity;
import huy.project.profile_service.core.domain.exception.AppException;
import huy.project.profile_service.core.port.IFriendPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteFriendUseCase {
    private final IFriendPort friendPort;

    @Transactional(rollbackFor = Exception.class)
    public void deleteFriend(Long touristId, Long friendId) {
        FriendEntity friend = friendPort.getFriendById(friendId);
        if (friend == null) {
            log.error("friend not found with id: {}", friendId);
            throw new AppException(ErrorCode.FRIEND_NOT_FOUND);
        }
        if (!friend.getTouristId().equals(touristId)) {
            log.error("touristId: {} is not friend with : {}", touristId, friendId);
            throw new AppException(ErrorCode.DELETE_FRIEND_FORBIDDEN);
        }

        friendPort.deleteFriendById(friendId);
    }
}
