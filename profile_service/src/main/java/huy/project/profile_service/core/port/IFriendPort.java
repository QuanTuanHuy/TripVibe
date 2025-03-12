package huy.project.profile_service.core.port;

import huy.project.profile_service.core.domain.entity.FriendEntity;

import java.util.List;

public interface IFriendPort {
    FriendEntity save(FriendEntity friend);
    List<FriendEntity> getFriendsByTouristId(Long touristId);
    FriendEntity getFriendById(Long id);
    void deleteFriendById(Long id);
}
