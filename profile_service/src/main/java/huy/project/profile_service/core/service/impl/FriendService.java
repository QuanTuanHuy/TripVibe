package huy.project.profile_service.core.service.impl;

import huy.project.profile_service.core.domain.dto.request.CreateFriendDto;
import huy.project.profile_service.core.domain.entity.FriendEntity;
import huy.project.profile_service.core.service.IFriendService;
import huy.project.profile_service.core.usecase.CreateFriendUseCase;
import huy.project.profile_service.core.usecase.DeleteFriendUseCase;
import huy.project.profile_service.core.usecase.GetFriendUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService implements IFriendService {
    private final CreateFriendUseCase createFriendUseCase;
    private final GetFriendUseCase getFriendUseCase;
    private final DeleteFriendUseCase deleteFriendUseCase;

    @Override
    public FriendEntity createFriend(Long touristId, CreateFriendDto req) {
        return createFriendUseCase.createFriend(touristId, req);
    }

    @Override
    public List<FriendEntity> getFriendsByTouristId(Long touristId) {
        return getFriendUseCase.getFriendsByTouristId(touristId);
    }

    @Override
    public void deleteFriend(Long touristId, Long friendId) {
        deleteFriendUseCase.deleteFriend(touristId, friendId);
    }
}
