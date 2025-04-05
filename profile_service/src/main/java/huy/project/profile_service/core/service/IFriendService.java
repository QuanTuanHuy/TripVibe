package huy.project.profile_service.core.service;

import huy.project.profile_service.core.domain.dto.request.CreateFriendDto;
import huy.project.profile_service.core.domain.entity.FriendEntity;

import java.util.List;

public interface IFriendService {
    FriendEntity createFriend(Long touristId, CreateFriendDto req);
    List<FriendEntity> getFriendsByTouristId(Long touristId);
    void deleteFriend(Long touristId, Long friendId);
}
